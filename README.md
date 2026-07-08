# Teacher Reading Assignment Portal

A full-stack reading assignment portal for teachers and students.

Teachers can assign books, review student progress, and track assignment status. Students can sign in, view assigned reading, open seeded book content, update reading minutes, and mark assignments as not started, in progress, or completed.

This project was built for the Teacher Reading Assignment Portal coding challenge.

## Project Overview

The application has two authenticated user roles:

- `TEACHER`: creates reading assignments and monitors progress.
- `STUDENT`: views assigned books and updates reading progress.

The backend exposes a secured REST API using JWT bearer tokens. The frontend is a React + TypeScript dashboard with light and dark mode, status chips, progress bars, book cards, and role-specific views.

## Tech Stack

Backend:

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT bearer authentication
- PostgreSQL
- Flyway migrations
- Bean Validation
- Lombok
- Springdoc OpenAPI / Swagger UI

Frontend:

- React
- TypeScript
- Vite
- Axios
- CSS custom properties for light/dark themes

## Project Structure

```text
teacher-reading-project/
├── teacher-reading-portal/   # Spring Boot backend
├── teacher-reading-ui/       # React + TypeScript frontend
├── ARCHITECTURE.md
└── README.md
```

Backend packages:

```text
src/main/java/com/njenga/teacher_reading_portal/
├── assignment
├── auth
├── book
├── common
├── config
└── user
```

Frontend folders:

```text
src/
├── api
├── components
├── pages
├── types
└── App.tsx
```

## Backend Setup

Prerequisites:

- Java 21
- PostgreSQL
- Maven wrapper is included in `teacher-reading-portal`

Create the local database:

```bash
createdb teacher_reading_portal
```

The local datasource defaults are in `teacher-reading-portal/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/teacher_reading_portal
    username: postgres
    password: postgres
```

Run the backend:

```bash
cd teacher-reading-portal
./mvnw spring-boot:run
```

Run backend tests:

```bash
cd teacher-reading-portal
./mvnw test
```

Backend runs at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Frontend Setup

Prerequisites:

- Node.js
- npm

Install dependencies:

```bash
cd teacher-reading-ui
npm install
```

Run the frontend:

```bash
cd teacher-reading-ui
npm run dev
```

Build the frontend:

```bash
cd teacher-reading-ui
npm run build
```

Frontend runs at:

```text
http://localhost:5173
```

## Demo Credentials

All seeded demo users use this password:

```text
password
```

Teacher:

```text
teacher@example.com
```

Students:

```text
student@example.com
alice@example.com
ben@example.com
grace@example.com
```

## API Endpoints

Authentication:

```http
POST /api/auth/login
```

Request:

```json
{
  "email": "teacher@example.com",
  "password": "password"
}
```

Response includes a JWT:

```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "name": "Teacher Demo",
    "email": "teacher@example.com",
    "role": "TEACHER"
  }
}
```

Use the token as:

```http
Authorization: Bearer jwt-token
```

Books:

```http
GET /api/books
```

Users:

```http
GET /api/users/students
```

Assignments:

```http
POST /api/assignments
GET /api/assignments/teacher
GET /api/assignments/student
PATCH /api/assignments/{id}/progress
```

Create assignment request:

```json
{
  "bookId": 1,
  "studentId": 2,
  "dueDate": "2026-07-20"
}
```

Update progress request:

```json
{
  "status": "IN_PROGRESS",
  "minutesRead": 70
}
```

Assignment statuses:

```text
NOT_STARTED
IN_PROGRESS
COMPLETED
```

## Swagger JWT Testing

Swagger UI supports bearer-token authentication.

1. Open `http://localhost:8080/swagger-ui.html`.
2. Call `POST /api/auth/login`.
3. Copy the returned `token`.
4. Click `Authorize`.
5. Paste only the JWT value.
6. Call protected endpoints from Swagger.

## Screenshots

Login:

<img width="1800" height="1169" alt="Login screen" src="https://github.com/user-attachments/assets/c78f67cc-1e00-4492-a760-adddc594bb02" />

Teacher dashboard:

<img width="1800" height="1169" alt="Teacher dashboard" src="https://github.com/user-attachments/assets/adf164aa-2f75-4433-b7b5-fbf61eb8291b" />

Student dashboard:

<img width="1800" height="1169" alt="Student dashboard" src="https://github.com/user-attachments/assets/f5b0046b-3972-4842-9282-50fa5c1e3d6f" />

## Seed Data

Flyway creates and seeds:

- Demo teacher and student users
- Five books with multi-paragraph content
- Assignments across all statuses
- Known demo passwords for local testing

Migrations are stored in:

```text
teacher-reading-portal/src/main/resources/db/migration
```

## Deployment Notes

Recommended production changes:

- Set `app.security.jwt.secret` from an environment variable.
- Set database URL, username, and password from environment variables.
- Set allowed CORS origins to the deployed frontend URL.
- Disable SQL logging in production.
- Use a managed PostgreSQL database.
- Build the React app with the deployed API base URL.
- Serve the frontend from a static host such as Vercel, Netlify, or an S3-compatible host.
- Deploy the backend to a Java-capable host such as Render, Railway, Fly.io, or a container platform.

Suggested backend environment variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
APP_SECURITY_JWT_SECRET
```

Suggested frontend environment variable:

```text
VITE_API_BASE_URL
```

## Verification

Commands used during development:

```bash
cd teacher-reading-portal
./mvnw test
```

```bash
cd teacher-reading-ui
npm run build
```

Current backend test coverage includes service-level tests for books, users, and assignments, plus a Spring context load test.

## Known Future Improvements

- Add `GET /api/auth/me` to verify stored sessions on page refresh.
- Add frontend 401 handling for expired tokens.
- Move API base URL and CORS origins fully to environment-based configuration.
- Add integration tests for authentication and role-based access.
- Add real deployment profiles for local and production environments.
