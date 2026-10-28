"""Regras, validação e transações para os cadastros da Sprint 2."""

import re

from sqlalchemy.exc import IntegrityError

from sprint2.dao import InstitutionDAO, PartnerDAO, StudentDAO
from sprint2.database import SessionLocal
from sprint2.models import Partner, Student
from sprint2.security import hash_password


class ValidationError(Exception):
    pass


class NotFoundError(Exception):
    pass


class ConflictError(Exception):
    pass


EMAIL_PATTERN = re.compile(r"^[^\s@]+@[^\s@]+\.[^\s@]+$")


def _text(payload, field, label, required=True, limit=None):
    value = payload.get(field)
    if value is None:
        value = ""
    if not isinstance(value, str):
        value = str(value)
    value = value.strip()
    if required and not value:
        raise ValidationError("Informe {}.".format(label))
    if limit is not None and len(value) > limit:
        raise ValidationError("{} deve ter no máximo {} caracteres.".format(label, limit))
    return value


def _email(payload):
    value = _text(payload, "email", "um email", limit=254).lower()
    if not EMAIL_PATTERN.match(value):
        raise ValidationError("Informe um email válido.")
    return value


def _login(payload):
    value = _text(payload, "login", "um login", limit=80)
    if len(value) < 3:
        raise ValidationError("O login deve ter pelo menos 3 caracteres.")
    return value


def _password(payload, required):
    value = _text(payload, "senha", "uma senha", required=required, limit=256)
    if value and len(value) < 8:
        raise ValidationError("A senha deve ter pelo menos 8 caracteres.")
    return value


def _student_values(payload, session, current=None):
    values = {
        "name": _text(payload, "nome", "o nome", limit=160),
        "email": _email(payload),
        "cpf": _text(payload, "cpf", "o CPF", limit=14),
        "rg": _text(payload, "rg", "o RG", limit=30),
        "address": _text(payload, "endereco", "o endereço", limit=240),
        "course": _text(payload, "curso", "o curso", limit=160),
        "login": _login(payload),
    }
    try:
        institution_id = int(payload.get("instituicao_id"))
    except (TypeError, ValueError):
        raise ValidationError("Selecione uma instituição válida.")
    if InstitutionDAO.get(session, institution_id) is None:
        raise ValidationError("A instituição selecionada não está cadastrada.")
    values["institution_id"] = institution_id
    password = _password(payload, required=current is None)
    if password:
        values["password_hash"] = hash_password(password)

    user_id = current.user_id if current else None
    if StudentDAO.login_exists(session, values["login"], user_id):
        raise ConflictError("Este login já está em uso.")
    for field, label in ((Student.cpf, "CPF"), (Student.email, "email")):
        if StudentDAO.field_exists(session, field, values[field.key], user_id):
            raise ConflictError("Já existe um aluno cadastrado com este {}.".format(label))
    return values


def _partner_values(payload, session, current=None):
    values = {
        "name": _text(payload, "nome", "o nome da empresa", limit=160),
        "email": _email(payload),
        "login": _login(payload),
    }
    password = _password(payload, required=current is None)
    if password:
        values["password_hash"] = hash_password(password)

    user_id = current.user_id if current else None
    if PartnerDAO.login_exists(session, values["login"], user_id):
        raise ConflictError("Este login já está em uso.")
    if PartnerDAO.email_exists(session, values["email"], user_id):
        raise ConflictError("Já existe uma empresa cadastrada com este email.")
    return values


def _student_json(student):
    return {
        "id": student.user_id,
        "nome": student.name,
        "email": student.email,
        "cpf": student.cpf,
        "rg": student.rg,
        "endereco": student.address,
        "curso": student.course,
        "instituicao_id": student.institution_id,
        "instituicao": student.institution.name,
        "login": student.user.login,
    }


def _partner_json(partner):
    return {
        "id": partner.user_id,
        "nome": partner.name,
        "email": partner.email,
        "login": partner.user.login,
    }


def list_institutions():
    with SessionLocal() as session:
        return [
            {"id": institution.id, "nome": institution.name}
            for institution in InstitutionDAO.list_all(session)
        ]


def list_students(search=""):
    with SessionLocal() as session:
        return [_student_json(row) for row in StudentDAO.list_all(session, search.strip())]


def get_student(student_id):
    with SessionLocal() as session:
        student = StudentDAO.get(session, student_id)
        if student is None:
            raise NotFoundError("Aluno não encontrado.")
        return _student_json(student)


def create_student(payload):
    try:
        with SessionLocal.begin() as session:
            values = _student_values(payload, session)
            student = StudentDAO.create(session, values)
            return _student_json(student)
    except IntegrityError as error:
        raise ConflictError("Não foi possível salvar: verifique se login, CPF e email são únicos.") from error


def update_student(student_id, payload):
    try:
        with SessionLocal.begin() as session:
            student = StudentDAO.get(session, student_id)
            if student is None:
                raise NotFoundError("Aluno não encontrado.")
            values = _student_values(payload, session, student)
            student.name = values["name"]
            student.email = values["email"]
            student.cpf = values["cpf"]
            student.rg = values["rg"]
            student.address = values["address"]
            student.course = values["course"]
            student.institution_id = values["institution_id"]
            student.user.login = values["login"]
            if "password_hash" in values:
                student.user.password_hash = values["password_hash"]
            session.flush()
            return _student_json(student)
    except IntegrityError as error:
        raise ConflictError("Não foi possível salvar: verifique se login, CPF e email são únicos.") from error


def delete_student(student_id):
    with SessionLocal.begin() as session:
        student = StudentDAO.get(session, student_id)
        if student is None:
            raise NotFoundError("Aluno não encontrado.")
        session.delete(student.user)


def list_partners(search=""):
    with SessionLocal() as session:
        return [_partner_json(row) for row in PartnerDAO.list_all(session, search.strip())]


def get_partner(partner_id):
    with SessionLocal() as session:
        partner = PartnerDAO.get(session, partner_id)
        if partner is None:
            raise NotFoundError("Empresa parceira não encontrada.")
        return _partner_json(partner)


def create_partner(payload):
    try:
        with SessionLocal.begin() as session:
            values = _partner_values(payload, session)
            partner = PartnerDAO.create(session, values)
            return _partner_json(partner)
    except IntegrityError as error:
        raise ConflictError("Não foi possível salvar: verifique se login e email são únicos.") from error


def update_partner(partner_id, payload):
    try:
        with SessionLocal.begin() as session:
            partner = PartnerDAO.get(session, partner_id)
            if partner is None:
                raise NotFoundError("Empresa parceira não encontrada.")
            values = _partner_values(payload, session, partner)
            partner.name = values["name"]
            partner.email = values["email"]
            partner.user.login = values["login"]
            if "password_hash" in values:
                partner.user.password_hash = values["password_hash"]
            session.flush()
            return _partner_json(partner)
    except IntegrityError as error:
        raise ConflictError("Não foi possível salvar: verifique se login e email são únicos.") from error


def delete_partner(partner_id):
    with SessionLocal.begin() as session:
        partner = PartnerDAO.get(session, partner_id)
        if partner is None:
            raise NotFoundError("Empresa parceira não encontrada.")
        session.delete(partner.user)
