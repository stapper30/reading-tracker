# Book Tracker API

A FastAPI backend for tracking books per user with JWT authentication.

## Features

- User registration and login
- JWT-protected book endpoints
- Per-user book isolation (users can only modify their own books)
- Add, list, mark as read, and delete books
- Optional cover image lookup using Google Books API when `image_url` is not provided

## Tech Stack

- Python
- FastAPI
- PostgreSQL (via `psycopg` connection pool)
- Passlib Argon2 password hashing
- JWT tokens (`python-jose`)

## Project Structure

- `main.py`: FastAPI app, routes, auth token creation
- `auth.py`: token validation and current user dependency
- `database.py`: DB pool setup and app lifespan hooks
- `repos.py`: database access layer for books and users
- `models.py`: domain models (`Book`, `User`)

## Prerequisites

- Python 3.10+
- PostgreSQL
- A virtual environment (recommended)

## Environment Variables

Create a `.env` file in the project root:

```env
DATABASE_URL=postgresql://<username>:<password>@<host>:<port>/<database>
SECRET_KEY=<strong-random-secret>
```

Notes:
- `SECRET_KEY` is used to sign JWTs.
- `DATABASE_URL` must point to a database containing the required tables shown below.

## Database Schema

Run equivalent SQL in your PostgreSQL database:

```sql
CREATE TABLE IF NOT EXISTS users (
		id SERIAL PRIMARY KEY,
		email TEXT UNIQUE NOT NULL,
		password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
		id SERIAL PRIMARY KEY,
		title TEXT NOT NULL,
		author TEXT NOT NULL,
		rating INTEGER NOT NULL,
		user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
		image_url TEXT,
		type TEXT NOT NULL
);
```

## Installation

1. Create and activate a virtual environment.
2. Install dependencies:

```bash
pip install fastapi uvicorn psycopg[binary,pool] passlib[argon2] python-jose python-dotenv python-multipart httpx
```

## Run Locally

Start the API server:

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

Base URL:

```text
http://127.0.0.1:8000
```

## Authentication Flow

1. Register with `POST /users/register`.
2. Login with `POST /users/login` using form data (`username`, `password`).
3. Use the returned bearer token for protected routes:

```text
Authorization: Bearer <access_token>
```

## API Endpoints

### Public

- `POST /users/register`
- `POST /users/login`

### Protected (Bearer Token Required)

- `GET /books`
- `POST /books/add`
- `POST /books/mark_read/{id}`
- `DELETE /books/delete/{id}`

## Example Requests

Register:

```bash
curl -X POST http://127.0.0.1:8000/users/register \
	-H "Content-Type: application/json" \
	-d '{"email":"user@example.com","password":"StrongPass123"}'
```

Login:

```bash
curl -X POST http://127.0.0.1:8000/users/login \
	-H "Content-Type: application/x-www-form-urlencoded" \
	-d "username=user@example.com&password=StrongPass123"
```

Add a book (authenticated):

```bash
curl -X POST http://127.0.0.1:8000/books/add \
	-H "Authorization: Bearer <access_token>" \
	-H "Content-Type: application/json" \
	-d '{"title":"Dune","author":"Frank Herbert","rating":9,"type":"to_read"}'
```

Mark as read:

```bash
curl -X POST http://127.0.0.1:8000/books/mark_read/1 \
	-H "Authorization: Bearer <access_token>" \
	-H "Content-Type: application/json" \
	-d '8'
```

## Notes and Current Behavior

- Token expiry is set to 24 days.
- Book ratings are validated in code to be between 0 and 10.
- `POST /books/mark_read/{id}` expects the request body to be a raw JSON integer (for example, `8`), not an object.
- If `image_url` is omitted while adding a book, the API attempts to fetch a thumbnail from Google Books.