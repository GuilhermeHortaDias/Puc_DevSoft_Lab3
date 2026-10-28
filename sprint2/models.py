"""Entidades ORM alinhadas ao modelo de classes da Sprint 1."""

from datetime import datetime

from sqlalchemy import CheckConstraint, DateTime, ForeignKey, Integer, String, UniqueConstraint
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column, relationship


class Base(DeclarativeBase):
    pass


class User(Base):
    """Superclasse de identidade; guarda login e hash de senha."""

    __tablename__ = "usuarios"
    __table_args__ = (
        CheckConstraint(
            "tipo IN ('ALUNO', 'EMPRESA', 'PROFESSOR')", name="ck_usuarios_tipo"
        ),
    )

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    login: Mapped[str] = mapped_column(String(80), nullable=False, unique=True)
    password_hash: Mapped[str] = mapped_column("senha_hash", String(256), nullable=False)
    role: Mapped[str] = mapped_column("tipo", String(20), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        "criado_em", DateTime, nullable=False, default=datetime.utcnow
    )

    student: Mapped["Student"] = relationship(
        back_populates="user", cascade="all, delete-orphan", uselist=False
    )
    partner: Mapped["Partner"] = relationship(
        back_populates="user", cascade="all, delete-orphan", uselist=False
    )


class Institution(Base):
    __tablename__ = "instituicoes"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    name: Mapped[str] = mapped_column("nome", String(160), nullable=False, unique=True)

    students: Mapped[list["Student"]] = relationship(back_populates="institution")


class Student(Base):
    __tablename__ = "alunos"
    __table_args__ = (
        UniqueConstraint("cpf", name="uq_alunos_cpf"),
        UniqueConstraint("email", name="uq_alunos_email"),
    )

    user_id: Mapped[int] = mapped_column(
        "usuario_id", ForeignKey("usuarios.id", ondelete="CASCADE"), primary_key=True
    )
    name: Mapped[str] = mapped_column("nome", String(160), nullable=False)
    email: Mapped[str] = mapped_column(String(254), nullable=False)
    cpf: Mapped[str] = mapped_column(String(14), nullable=False)
    rg: Mapped[str] = mapped_column(String(30), nullable=False)
    address: Mapped[str] = mapped_column("endereco", String(240), nullable=False)
    course: Mapped[str] = mapped_column("curso", String(160), nullable=False)
    institution_id: Mapped[int] = mapped_column(
        "instituicao_id", ForeignKey("instituicoes.id", ondelete="RESTRICT"), nullable=False
    )

    user: Mapped[User] = relationship(back_populates="student")
    institution: Mapped[Institution] = relationship(back_populates="students")


class Partner(Base):
    __tablename__ = "empresas_parceiras"
    __table_args__ = (UniqueConstraint("email", name="uq_empresas_email"),)

    user_id: Mapped[int] = mapped_column(
        "usuario_id", ForeignKey("usuarios.id", ondelete="CASCADE"), primary_key=True
    )
    name: Mapped[str] = mapped_column("nome", String(160), nullable=False)
    email: Mapped[str] = mapped_column(String(254), nullable=False)

    user: Mapped[User] = relationship(back_populates="partner")
