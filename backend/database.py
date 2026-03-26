import os
from psycopg_pool import AsyncConnectionPool
from contextlib import asynccontextmanager
from fastapi import FastAPI
import os

DB_URL = os.environ.get("DATABASE_URL")

pool = AsyncConnectionPool(conninfo=DB_URL, open=False)

@asynccontextmanager
async def lifespan(app: FastAPI):
    await pool.open()
    yield
    await pool.close(timeout=5)