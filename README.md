# Let's Play - REST API

A secure RESTful API built with Spring Boot, MongoDB, and JWT authentication for managing users and products.

## Features

- JWT-based authentication and authorization
- User registration and login
- Product CRUD operations
- Role-based access control (USER, ADMIN)
- Input validation and error handling
- BCrypt password hashing
- CORS support for frontend integration
- Comprehensive API documentation

## Tech Stack

- **Framework**: Spring Boot 4.0.1
- **Database**: MongoDB
- **Security**: Spring Security + JWT
- **Validation**: Bean Validation
- **Build Tool**: Maven
- **Java Version**: 25

## Prerequisites

- Java 25 or higher
- Maven 3.6+
- MongoDB (local or Atlas)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/johneliud/lets-play
cd lets-play
```

### 2. Configure Environment Variables

Create a `application-secrets.properties` file in the resource directory with the details below:

```bash
# MongoDB Configuration
spring.mongodb.uri=<your-mongo-configuration-string>

# JWT Configuration
jwt.secret=<your-jwt-token>

# Security Configuration
spring.security.user.name=<your-security-username>
spring.security.user.password=<your-security-password>
```

### 3. Load Environment Variables

```bash
source .env
```

### 4. Build the Project

```bash
./mvnw clean install
```

### 5. Run the Application

```bash
./mvnw spring-boot:run
```

The API will start on `http://localhost:8080`

## API Documentation

### Base URL

```
http://localhost:8080
```

### Authentication

The API uses JWT (JSON Web Token) for authentication. After login, include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

Token expires in 24 hours.

## API Endpoints

### Authentication Endpoints

#### Register User

```http
POST /auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (201 Created):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john@example.com",
  "name": "John Doe",
  "role": "USER"
}
```

#### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john@example.com",
  "name": "John Doe",
  "role": "USER"
}
```

### Product Endpoints

#### Get All Products (Public)

```http
GET /products
```

**Response (200 OK):**

```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "name": "Sample Product",
    "description": "Product description",
    "price": 29.99,
    "userId": "507f1f77bcf86cd799439012",
    "userName": "John Doe",
    "createdAt": "2024-01-13T10:30:00",
    "updatedAt": "2024-01-13T10:30:00"
  }
]
```

#### Get Product by ID (Public)

```http
GET /products/{id}
```

**Response (200 OK):** Same format as above

#### Create Product (Authenticated)

```http
POST /products
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "name": "New Product",
  "description": "Product description",
  "price": 49.99
}
```

**Response (201 Created):** Product object

#### Update Product (Owner or Admin)

```http
PUT /products/{id}
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "name": "Updated Product",
  "description": "Updated description",
  "price": 59.99
}
```

**Response (200 OK):** Updated product object

#### Delete Product (Owner or Admin)

```http
DELETE /products/{id}
Authorization: Bearer <your-jwt-token>
```

**Response (204 No Content)**

### User Management Endpoints (Admin Only)

#### Get All Users

```http
GET /users
Authorization: Bearer <admin-jwt-token>
```

**Response (200 OK):**

```json
[
  {
    "id": "507f1f77bcf86cd799439012",
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2024-01-13T10:00:00",
    "updatedAt": "2024-01-13T10:00:00"
  }
]
```

#### Get User by ID

```http
GET /users/{id}
Authorization: Bearer <admin-jwt-token>
```

#### Update User

```http
PUT /users/{id}
Content-Type: application/json
Authorization: Bearer <admin-jwt-token>

{
  "name": "Updated Name",
  "email": "updated@example.com",
  "password": "newpassword123"
}
```

#### Delete User

```http
DELETE /users/{id}
Authorization: Bearer <admin-jwt-token>
```

**Response (204 No Content)**

## Error Responses

All errors follow a consistent format:

```json
{
  "timestamp": "2024-01-13T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "path": "/products/invalid-id"
}
```

### HTTP Status Codes

- **200 OK**: Successful GET, PUT operations
- **201 Created**: Successful POST operations
- **204 No Content**: Successful DELETE operations
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate resource (e.g., email already exists)
- **500 Internal Server Error**: Server error

### Validation Error Example

```json
{
  "timestamp": "2024-01-13T15:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/auth/register",
  "validationErrors": [
    "email: Email should be valid",
    "password: Password must be at least 8 characters"
  ]
}
```

## Testing with Postman/Insomnia

### 1. Register a User

- POST to `/auth/register`
- Save the returned token

### 2. Login

- POST to `/auth/login`
- Copy the JWT token from response

### 3. Test Authenticated Endpoints

- Add header: `Authorization: Bearer <token>`
- Create, update, delete products

### 4. Test Authorization

- Try accessing admin endpoints as regular user (should get 403)
- Try modifying another user's product (should get 403)

### 5. Test Validation

- Send invalid data (empty fields, invalid email, short password)
- Verify proper 400 error responses

## Project Structure

```
src/main/java/io/github/johneliud/letsplay/
├── config/              # Security and MongoDB configuration
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
│   ├── auth/           # Authentication DTOs
│   ├── error/          # Error response DTOs
│   ├── product/        # Product DTOs
│   └── user/           # User DTOs
├── exception/          # Custom exceptions
├── model/              # Entity models
├── repository/         # MongoDB repositories
├── security/           # JWT and security components
└── service/            # Business logic
    └── impl/           # Service implementations
```

## Security Features

- BCrypt password hashing
- JWT token-based authentication
- Role-based access control
- Input validation
- MongoDB injection prevention
- CORS configuration
- No sensitive data in responses
- Secure error handling

## Development

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
./mvnw clean package
java -jar target/lets-play-0.0.1-SNAPSHOT.jar
```

## Environment Configuration

### Development

- Use `.env` file for local development
- MongoDB can be local or Atlas

### Production

- Use environment variables or secure vault
- Enable HTTPS
- Restrict CORS to specific domains
- Use strong JWT secret (minimum 256 bits)
- Enable rate limiting
- Set up monitoring and logging

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the MIT License.

## Acknowledgments

Built with Spring Boot, MongoDB, and JWT for secure REST API development.