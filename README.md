# Book Tracker

Full-stack personal reading tracker:

- Backend: FastAPI + PostgreSQL + JWT auth
- Frontend: Android app (Kotlin + Jetpack Compose + Retrofit)

## Project Layout

- `backend/`: FastAPI API server and database access
- `android/`: Android client app

## Features

- User registration and login
- JWT-based authentication
- Per-user book lists
- Add books, delete books, mark as read, and fetch all books
- Optional cover image lookup from Google Books API when image URL is omitted

## Backend Setup (`backend/`)

### Prerequisites

- Python 3.10+
- PostgreSQL

### Environment Variables

Create `backend/.env`:

```env
DATABASE_URL=postgresql://<username>:<password>@<host>:<port>/<database>
SECRET_KEY=<strong-random-secret>
```

### Database Schema

Run this SQL in your PostgreSQL database:

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

### Install Dependencies

From the repo root:

```bash
python -m venv .venv
.venv\\Scripts\\activate
pip install fastapi uvicorn psycopg[binary,pool] passlib[argon2] python-jose python-dotenv python-multipart httpx
```

### Run Backend

From `backend/`:

```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

API base URL:

```text
http://127.0.0.1:8000
```

## Frontend Setup (`android/`)

### Prerequisites

- Android Studio (latest stable)
- Android SDK / emulator (or physical device)
- JDK 11+

### Configure API URL

Set the backend base URL in:

- `android/app/src/main/java/com/example/books/RetrofitClient.kt`

Current config uses ngrok URL:

```kotlin
private const val BASE_URL = "https://<your-ngrok-or-api-domain>/"
```

If using Android emulator with local backend, use:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"
```

If using a real device, use your machine IP or a tunnel (for example ngrok).

### Run Frontend

Option 1 (recommended):

1. Open `android/` in Android Studio.
2. Sync Gradle.
3. Run app on emulator/device.

Option 2 (CLI):

```bash
cd android
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
cd android
.\gradlew.bat assembleDebug
```

## Running Full Stack

1. Start PostgreSQL.
2. Start backend from `backend/` on port 8000.
3. Ensure Android `BASE_URL` points to the running backend (local or ngrok).
4. Launch Android app and log in/register.

## API Overview

Public:

- `POST /users/register`
- `POST /users/login` (form-url-encoded: username/password)

Authenticated (Bearer token):

- `GET /books`
- `POST /books/add`
- `POST /books/mark_read/{id}`
- `DELETE /books/delete/{id}`

Auth header format:

```text
Authorization: Bearer <access_token>
```

## Notes

- JWT expiry is currently 24 days.
- Rating must be between 0 and 10.
- `POST /books/mark_read/{id}` expects a raw JSON integer body (example: `8`).
- If book `image_url` is missing, backend attempts Google Books thumbnail lookup.