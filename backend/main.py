import os
from datetime import datetime, timedelta, timezone

from dotenv import load_dotenv
from fastapi import FastAPI, Request, Response
from fastapi.exceptions import RequestValidationError
from fastapi.params import Body, Depends
from fastapi.security import OAuth2PasswordRequestForm
from jose import jwt
from passlib.context import CryptContext
from pydantic import BaseModel
from typing_extensions import Annotated

from auth import get_current_user
from database import lifespan, pool
from models import Book, User
from repos import BookRepo, UserRepo

load_dotenv()

app = FastAPI(lifespan=lifespan)

SECRET_KEY = os.environ.get("SECRET_KEY", '')
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24 * 24


class BookPydantic(BaseModel):
    title: str
    author: str
    rating: int
    type: str
    image_url: str | None = None
    id: int | None = None


class UserPydantic(BaseModel):
    email: str
    password: str


def hash(password: str) -> str:
    pwd_context = CryptContext(schemes=["argon2"], deprecated="auto")
    return pwd_context.hash(password)


def verify(plain_password: str, hashed_password: str) -> bool:
    pwd_context = CryptContext(schemes=["argon2"], deprecated="auto")
    return pwd_context.verify(plain_password, hashed_password)


def create_access_token(data: dict):
    to_encode = data.copy()

    # Calculate expiration time
    expire = datetime.now(timezone.utc) + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)

    # "sub" is the standard field for the subject (user identity)
    to_encode.update({"exp": expire})

    # Sign the token
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    # Get the request body as bytes
    body = await request.body()

    # Print or log the body and the error details
    print(f"422 Error: {exc.errors()}")
    print(f"Request body: {body.decode()}")

    return {"fail": "failed validation"}


@app.get("/books")
async def get_books(current_user: Annotated[User, Depends(get_current_user)]):
    bookRepo = BookRepo(db_pool=pool)
    if not current_user.id:
        return Response({"Server error"}, 500)
    books = await bookRepo.get_books_for_user(current_user.id)
    return [book.to_dict() for book in books]


@app.post("/books/add")
async def add_book(
    bookPost: BookPydantic, current_user: Annotated[User, Depends(get_current_user)]
):
    print(f"User {current_user.email} is adding a book: {bookPost}")
    bookRepo = BookRepo(db_pool=pool)
    book = Book(
        title=bookPost.title,
        author=bookPost.author,
        rating=bookPost.rating,
        user_id=current_user.id,
        type=bookPost.type,
        image_url=bookPost.image_url,
    )
    await bookRepo.add_book(book)
    if not current_user.id:
        return Response({"Server error"}, 500)
    books = await bookRepo.get_books_for_user(current_user.id)
    return [book.to_dict() for book in books]


@app.post("/books/mark_read/{id}")
async def mark_book_as_read(
    id: int,
    rating: Annotated[int, Body()],
    current_user: Annotated[User, Depends(get_current_user)],
):
    print(
        f"User {current_user.email} is marking book id {id} as read with rating {rating}"
    )
    bookRepo = BookRepo(db_pool=pool)
    if not current_user.id:
        return Response(status_code=500)
    await bookRepo.mark_book_as_read(id, rating, current_user.id)
    books = await bookRepo.get_books_for_user(current_user.id)
    return [book.to_dict() for book in books]


@app.post("/users/register")
async def register_user(userPost: UserPydantic):
    userRepo = UserRepo(db_pool=pool)
    password_hash = hash(userPost.password)
    await userRepo.add_user(email=userPost.email, password_hash=password_hash)
    return {"status": "User registered successfully"}


@app.post("/users/login")
async def login_user(form_data: Annotated[OAuth2PasswordRequestForm, Depends()]):
    userRepo = UserRepo(db_pool=pool)
    user = await userRepo.get_user_by_email(email=form_data.username)
    if user is None:
        return {"error": "Invalid credentials"}
    if verify(form_data.password, user.password_hash):
        access_token = create_access_token(data={"sub": user.email})
        return {"access_token": access_token, "token_type": "bearer"}
    else:
        return {"error": "Invalid credentials"}


@app.delete("/books/delete/{id}")
async def delete_book(id: int, current_user: Annotated[User, Depends(get_current_user)]):
    bookRepo = BookRepo(db_pool=pool)
    if not current_user.id:
        return Response(status_code=500)
    await bookRepo.delete_book_by_id(id, current_user.id)
    books = await bookRepo.get_books_for_user(current_user.id)
    return [book.to_dict() for book in books]
