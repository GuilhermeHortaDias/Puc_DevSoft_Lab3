"""Inicializa o banco e executa a aplicação web local."""

import os
from http.server import ThreadingHTTPServer

from sprint2.controllers import ApplicationHandler
from sprint2.database import initialize_database


def main():
    initialize_database()
    host = os.environ.get("SPRINT2_HOST", "127.0.0.1")
    port = int(os.environ.get("SPRINT2_PORT", "8000"))
    server = ThreadingHTTPServer((host, port), ApplicationHandler)
    print("Sistema de Moeda Estudantil disponível em http://{}:{}".format(host, port))
    print("Use Ctrl+C para encerrar.")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nServidor encerrado.")
    finally:
        server.server_close()


if __name__ == "__main__":
    main()
