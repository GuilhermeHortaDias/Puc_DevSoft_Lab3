"""Controladores HTTP: conectam a interface, os serviços e a persistência."""

import json
import logging
import mimetypes
from http.server import BaseHTTPRequestHandler
from pathlib import Path
from urllib.parse import parse_qs, unquote, urlparse

from sqlalchemy.exc import IntegrityError

from sprint2 import services


LOGGER = logging.getLogger("sprint2")
STATIC_DIR = Path(__file__).resolve().parent / "static"
MAX_BODY_BYTES = 1024 * 1024


def _json_response(status, payload=None):
    return status, payload


def route_api(method, path, query, body):
    """Despacha os recursos REST dos CRUDs de aluno e empresa parceira."""
    if path == "/api/health" and method == "GET":
        return _json_response(200, {"status": "ok"})
    if path == "/api/instituicoes" and method == "GET":
        return _json_response(200, services.list_institutions())

    resources = {
        "/api/alunos": {
            "list": services.list_students,
            "get": services.get_student,
            "create": services.create_student,
            "update": services.update_student,
            "delete": services.delete_student,
        },
        "/api/empresas-parceiras": {
            "list": services.list_partners,
            "get": services.get_partner,
            "create": services.create_partner,
            "update": services.update_partner,
            "delete": services.delete_partner,
        },
    }

    for base_path, actions in resources.items():
        if path == base_path:
            if method == "GET":
                search = (query.get("busca") or [""])[0]
                return _json_response(200, actions["list"](search))
            if method == "POST":
                return _json_response(201, actions["create"](body))
            return _json_response(405, {"erro": "Método não permitido para este recurso."})

        prefix = base_path + "/"
        if path.startswith(prefix):
            raw_id = path[len(prefix):]
            if "/" in raw_id or not raw_id.isdigit():
                return _json_response(404, {"erro": "Recurso não encontrado."})
            record_id = int(raw_id)
            if method == "GET":
                return _json_response(200, actions["get"](record_id))
            if method == "PUT":
                return _json_response(200, actions["update"](record_id, body))
            if method == "DELETE":
                actions["delete"](record_id)
                return _json_response(204)
            return _json_response(405, {"erro": "Método não permitido para este recurso."})

    return _json_response(404, {"erro": "Rota não encontrada."})


class ApplicationHandler(BaseHTTPRequestHandler):
    server_version = "SistemaMoedaEstudantil/0.1"

    def do_GET(self):
        self._dispatch()

    def do_POST(self):
        self._dispatch()

    def do_PUT(self):
        self._dispatch()

    def do_DELETE(self):
        self._dispatch()

    def _dispatch(self):
        parsed = urlparse(self.path)
        path = unquote(parsed.path)
        if not path.startswith("/api/"):
            self._serve_static(path)
            return

        body = {}
        if self.command in ("POST", "PUT"):
            try:
                body = self._read_json()
            except ValueError as error:
                self._send_json(400, {"erro": str(error)})
                return

        try:
            status, payload = route_api(
                self.command, path, parse_qs(parsed.query), body
            )
        except services.ValidationError as error:
            status, payload = 400, {"erro": str(error)}
        except services.ConflictError as error:
            status, payload = 409, {"erro": str(error)}
        except services.NotFoundError as error:
            status, payload = 404, {"erro": str(error)}
        except IntegrityError:
            status, payload = 409, {"erro": "O registro viola uma restrição de integridade."}
        except Exception:
            LOGGER.exception("Falha ao processar %s %s", self.command, path)
            status, payload = 500, {"erro": "Não foi possível concluir a solicitação."}

        if status == 204:
            self.send_response(status)
            self.end_headers()
        else:
            self._send_json(status, payload)

    def _read_json(self):
        try:
            length = int(self.headers.get("Content-Length", "0"))
        except ValueError as error:
            raise ValueError("Tamanho da solicitação inválido.") from error
        if length > MAX_BODY_BYTES:
            raise ValueError("A solicitação excede o limite de 1 MB.")
        if length == 0:
            raise ValueError("Envie os dados do formulário em JSON.")
        if "application/json" not in self.headers.get("Content-Type", ""):
            raise ValueError("O conteúdo deve usar application/json.")
        try:
            payload = json.loads(self.rfile.read(length).decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError) as error:
            raise ValueError("O JSON enviado é inválido.") from error
        if not isinstance(payload, dict):
            raise ValueError("Envie um objeto JSON.")
        return payload

    def _send_json(self, status, payload):
        encoded = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(encoded)))
        self.send_header("X-Content-Type-Options", "nosniff")
        self.end_headers()
        self.wfile.write(encoded)

    def _serve_static(self, path):
        relative_path = "index.html" if path == "/" else path.lstrip("/")
        candidate = (STATIC_DIR / relative_path).resolve()
        try:
            candidate.relative_to(STATIC_DIR.resolve())
        except ValueError:
            self.send_error(404)
            return
        if not candidate.is_file():
            self.send_error(404)
            return
        content = candidate.read_bytes()
        content_type = mimetypes.guess_type(candidate.name)[0] or "application/octet-stream"
        if content_type.startswith("text/") or content_type in ("application/javascript",):
            content_type += "; charset=utf-8"
        self.send_response(200)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(content)))
        self.send_header("X-Content-Type-Options", "nosniff")
        self.end_headers()
        self.wfile.write(content)

    def log_message(self, format_string, *args):
        LOGGER.info("%s - %s", self.address_string(), format_string % args)
