"""Configuração do SQLite e inicialização do esquema da Sprint 2."""

import os
from pathlib import Path

from sqlalchemy import create_engine, event
from sqlalchemy.orm import sessionmaker

from sprint2.models import Base, Institution


PROJECT_ROOT = Path(__file__).resolve().parent.parent
DEFAULT_DATABASE = PROJECT_ROOT / "data" / "sprint2.sqlite3"
DATABASE_URL = os.environ.get(
    "SPRINT2_DATABASE_URL", "sqlite:///{}".format(DEFAULT_DATABASE.as_posix())
)

engine_options = {"future": True}
if DATABASE_URL.startswith("sqlite:"):
    engine_options["connect_args"] = {"check_same_thread": False}

engine = create_engine(DATABASE_URL, **engine_options)
SessionLocal = sessionmaker(bind=engine, expire_on_commit=False)


if DATABASE_URL.startswith("sqlite:"):
    @event.listens_for(engine, "connect")
    def enable_sqlite_foreign_keys(connection, _record):
        cursor = connection.cursor()
        cursor.execute("PRAGMA foreign_keys=ON")
        cursor.close()


def initialize_database():
    """Cria as tabelas e disponibiliza a instituição de demonstração."""
    if DATABASE_URL.startswith("sqlite:///"):
        database_path = DATABASE_URL[len("sqlite:///"):].split("?", 1)[0]
        if database_path and database_path != ":memory:":
            Path(database_path).expanduser().resolve().parent.mkdir(parents=True, exist_ok=True)

    Base.metadata.create_all(engine)
    with SessionLocal.begin() as session:
        exists = session.query(Institution).filter_by(name="PUC Minas").first()
        if exists is None:
            session.add(Institution(name="PUC Minas"))
