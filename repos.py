import httpx
from models import Book, User

class BookRepo:
    def __init__(self, db_pool):
        self.db_pool = db_pool

    async def _check_user_owns_book(self, book_id: int, user_id: int) -> bool:
        """Helper function to check if a user owns a specific book."""
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "SELECT user_id FROM books WHERE id = %s",
                    (book_id,)
                )
                row = await cur.fetchone()
                if row and row[0] == user_id:
                    return True
                return False

    async def add_book(self, book: Book):
        print(f"Adding book: {book}")
        if not book.image_url:
            book.image_url = self.fetch_image_url(book.title, book.author)
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "INSERT INTO books (title, author, rating, user_id, image_url, type) VALUES (%s, %s, %s, %s, %s, %s)",
                    (book.title, book.author, book.rating, book.user_id, book.image_url, book.type)
                )

    async def get_books(self):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute("SELECT * FROM books")
                rows = await cur.fetchall()
                return [Book.from_array(r) for r in rows]
            
    async def get_books_for_user(self, user_id: int):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "SELECT * FROM books WHERE user_id = %s",
                    (user_id,)
                )
                rows = await cur.fetchall()
                return [Book.from_array(r) for r in rows]
            
    async def get_book_by_title(self, title: str):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "SELECT * FROM books WHERE title = %s",
                    (title,)
                )
                row = await cur.fetchone()
                if row:
                    return Book.from_array(row)
                return None
            
    async def get_books_by_author(self, author: str):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "SELECT * FROM books WHERE author = %s",
                    (author,)
                )
                rows = await cur.fetchall()
                return [Book.from_array(r) for r in rows]
            
    async def get_book_by_id(self, book_id: int):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "SELECT * FROM books WHERE id = %s",
                    (book_id,)
                )
                row = await cur.fetchone()
                if row:
                    return Book.from_array(row)
                return None

    async def mark_book_as_read(self, id: str, rating: int, user_id: int):
        if not await self._check_user_owns_book(id, user_id):
            print(f"Unauthorized user {user_id} tried to mark book {id} as read")
            return
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "UPDATE books SET type = %s, rating = %s WHERE id = %s",
                    ("read", rating, id,)
                )    
    
    async def delete_book_by_title(self, title: str, user_id: int):
        # First get the book to check ownership
        book = await self.get_book_by_title(title)
        if not book or not await self._check_user_owns_book(book.id, user_id):
            print(f"Unauthorized user {user_id} tried to delete book with title '{title}'")
            return
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "DELETE FROM books WHERE title = %s",
                    (title,)
                )

    async def delete_book_by_id(self, book_id: int, user_id: int):
        if not await self._check_user_owns_book(book_id, user_id):
            print(f"Unauthorized user {user_id} tried to delete book {book_id}")
            return
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "DELETE FROM books WHERE id = %s",
                    (book_id,)
                )

    async def update_book_rating(self, title: str, new_rating: int, user_id: int):
        # First get the book to check ownership
        book = await self.get_book_by_title(title)
        if not book or not await self._check_user_owns_book(book.id, user_id):
            print(f"Unauthorized user {user_id} tried to update rating for book '{title}'")
            return
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "UPDATE books SET rating = %s WHERE title = %s",
                    (new_rating, title)
                )
    
    def fetch_image_url(self, title: str, author: str) -> str | None:
        print(f"Fetching image URL for {title} by {author}")
        try:
            with httpx.Client() as client:
                path = f"https://www.googleapis.com/books/v1/volumes?q=intitle:{title}+inauthor:{author}"
                print(f"Requesting URL: {path}")
                response = client.get(path)
                if response.status_code == 200:
                    data = response.json()
                    if "items" in data and len(data["items"]) > 0:
                        for item in data["items"]:
                            volume_info = item["volumeInfo"]
                            print(volume_info["title"], volume_info["authors"])
                            if volume_info["authors"] and author in volume_info["authors"]:
                                print(f"Fetched image URL for {title} by {author}")
                                image_links = volume_info["imageLinks"]
                                break
                        return image_links["thumbnail"]
                    return None
                return None
        except Exception as e:
            print(f"Error fetching image URL: {e}")

class UserRepo:
    def __init__(self, db_pool):
        self.db_pool = db_pool

    async def add_user(self, email: str, password_hash: str):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "INSERT INTO users (email, password_hash) VALUES (%s, %s)",
                    (email, password_hash)
                )

    async def get_user_by_email(self, email: str):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "SELECT email, password_hash, id FROM users WHERE email = %s",
                    (email,)
                )
                row = await cur.fetchone()
                if row:
                    return User(email=row[0], password_hash=row[1], id=row[2])
                return None
            
    async def get_user_by_email_and_password(self, email: str, password_hash: str):
        async with self.db_pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "SELECT email FROM users WHERE email = %s AND password_hash = %s",
                    (email, password_hash)
                )
                row = await cur.fetchone()
                if row:
                    return User(email=row[0])
                return None

    