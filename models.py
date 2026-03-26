class Book:
    id: int = None
    title: str
    author: str
    rating: int
    user_id: int
    image_url: str = None
    type: str

    def to_dict(self):
        return {
            "id": self.id,
            "title": self.title,
            "author": self.author,
            "rating": self.rating,
            "image_url": self.image_url,
            "type": self.type
        }
    
    def from_array(arr):
        return Book(
            id=arr[0],
            title=arr[1],
            author=arr[2],
            rating=arr[3],
            user_id=arr[4],
            image_url=arr[5],
            type=arr[6]
        )
    
    
    def __str__(self):
        return f"Book(title={self.title}, author={self.author}, rating={self.rating}, image_url={self.image_url}, type={self.type})"
    
    def __init__(self, title: str, author: str, rating: int, user_id: int, type: str, id: int = None, image_url: str = None):
        self.title = title
        self.author = author
        self.rating = rating
        self.user_id = user_id
        self.type = type
        self.id = id
        self.image_url = image_url

        if not (0 <= rating <= 10):
            raise ValueError("Rating must be between 0 and 10")
        
class User:
    id: int = None
    email: str
    password_hash: str

    def to_dict(self):
        return {
            "email": self.email
        }
    
    def __str__(self):
        return f"User(email={self.email})"
    
    def __init__(self, email: str, password_hash: str, id: int = None):
        self.email = email
        self.password_hash = password_hash
        self.id = id
