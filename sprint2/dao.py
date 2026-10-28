"""DAOs: consultas e persistência isoladas das regras de negócio."""

from sqlalchemy import or_, select
from sqlalchemy.orm import joinedload

from sprint2.models import Institution, Partner, Student, User


class InstitutionDAO:
    @staticmethod
    def list_all(session):
        return session.scalars(select(Institution).order_by(Institution.name)).all()

    @staticmethod
    def get(session, institution_id):
        return session.get(Institution, institution_id)


class StudentDAO:
    @staticmethod
    def list_all(session, search=""):
        statement = select(Student).options(
            joinedload(Student.user), joinedload(Student.institution)
        )
        if search:
            pattern = "%{}%".format(search)
            statement = statement.join(Student.user).where(
                or_(Student.name.ilike(pattern), Student.email.ilike(pattern), User.login.ilike(pattern))
            )
        return session.scalars(statement.order_by(Student.name)).all()

    @staticmethod
    def get(session, student_id):
        statement = (
            select(Student)
            .options(joinedload(Student.user), joinedload(Student.institution))
            .where(Student.user_id == student_id)
        )
        return session.scalars(statement).first()

    @staticmethod
    def login_exists(session, login, exclude_user_id=None):
        statement = select(User.id).where(User.login == login)
        if exclude_user_id is not None:
            statement = statement.where(User.id != exclude_user_id)
        return session.scalar(statement) is not None

    @staticmethod
    def field_exists(session, field, value, exclude_user_id=None):
        statement = select(Student.user_id).where(field == value)
        if exclude_user_id is not None:
            statement = statement.where(Student.user_id != exclude_user_id)
        return session.scalar(statement) is not None

    @staticmethod
    def create(session, values):
        user = User(login=values["login"], password_hash=values["password_hash"], role="ALUNO")
        student = Student(
            user=user,
            name=values["name"],
            email=values["email"],
            cpf=values["cpf"],
            rg=values["rg"],
            address=values["address"],
            course=values["course"],
            institution_id=values["institution_id"],
        )
        session.add(student)
        session.flush()
        return student


class PartnerDAO:
    @staticmethod
    def list_all(session, search=""):
        statement = select(Partner).options(joinedload(Partner.user))
        if search:
            pattern = "%{}%".format(search)
            statement = statement.join(Partner.user).where(
                or_(Partner.name.ilike(pattern), Partner.email.ilike(pattern), User.login.ilike(pattern))
            )
        return session.scalars(statement.order_by(Partner.name)).all()

    @staticmethod
    def get(session, partner_id):
        statement = (
            select(Partner)
            .options(joinedload(Partner.user))
            .where(Partner.user_id == partner_id)
        )
        return session.scalars(statement).first()

    @staticmethod
    def login_exists(session, login, exclude_user_id=None):
        statement = select(User.id).where(User.login == login)
        if exclude_user_id is not None:
            statement = statement.where(User.id != exclude_user_id)
        return session.scalar(statement) is not None

    @staticmethod
    def email_exists(session, email, exclude_user_id=None):
        statement = select(Partner.user_id).where(Partner.email == email)
        if exclude_user_id is not None:
            statement = statement.where(Partner.user_id != exclude_user_id)
        return session.scalar(statement) is not None

    @staticmethod
    def create(session, values):
        user = User(login=values["login"], password_hash=values["password_hash"], role="EMPRESA")
        partner = Partner(user=user, name=values["name"], email=values["email"])
        session.add(partner)
        session.flush()
        return partner
