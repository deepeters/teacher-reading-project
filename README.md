# Teacher Reading Assignment Portal

A full-stack web application that enables teachers to assign books to students, monitor reading progress, and allow students to track and update their reading assignments.

This project was built as a solution to the **Teacher Reading Assignment Portal Coding Challenge**.

---

# Overview

The objective of this project is to demonstrate the design and implementation of a complete web application within a limited time while making pragmatic engineering decisions.

The application currently supports two user roles:

- **Teacher**
- **Student**

Teachers can assign books to students and monitor their reading progress.

Students can view their assigned reading, open the assigned book, update the amount of time spent reading, and mark assignments as completed.

---

# Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Bean Validation
- Lombok

## Frontend

- React
- TypeScript
- Vite
- Axios

---

# Project Architecture

```text
                React + TypeScript
                        │
                 REST API (JSON)
                        │
                Spring Boot Backend
                        │
              Spring Data JPA
                        │
                  PostgreSQL
```

The backend exposes RESTful APIs consumed by the React frontend.

The application follows a layered architecture:

```text
Controller
     │
Service
     │
Repository
     │
Database
```

This separation keeps business logic isolated from persistence and presentation concerns.

---

# Domain Model

## User

Represents both teachers and students.

Fields

- id
- name
- email
- password
- role

Roles

- TEACHER
- STUDENT

---

## Book

Represents a book available for assignment.

Fields

- id
- title
- author
- description
- content

Books are seeded into the database and are read-only in this MVP.

---

## Assignment

Represents a reading assignment.

Fields

- teacher
- student
- book
- due date
- status
- minutes read
- timestamps

Status values

- NOT_STARTED
- IN_PROGRESS
- COMPLETED

---

# Features Implemented

## Backend

### Books

- Retrieve available books

### Students

- Retrieve available students

### Assignments

- Create assignment
- Retrieve teacher assignments
- Retrieve student assignments
- Update assignment progress
- Update minutes read

### Persistence

- PostgreSQL database
- Flyway schema migrations
- Seed data
<img width="1800" height="1169" alt="Screenshot 2026-07-08 at 18 07 23" src="https://github.com/user-attachments/assets/c78f67cc-1e00-4492-a760-adddc594bb02" />

---

## Frontend

### Teacher Dashboard

- View books
- View students
- Create assignments
- View assignment progress
<img width="1800" height="1169" alt="Screenshot 2026-07-08 at 18 07 35" src="https://github.com/user-attachments/assets/adf164aa-2f75-4433-b7b5-fbf61eb8291b" />


### Student Dashboard

- View assigned books
- Open book
- Read seeded content
- Update assignment status
- Update minutes read
<img width="1800" height="1169" alt="Screenshot 2026-07-08 at 18 07 56" src="https://github.com/user-attachments/assets/f5b0046b-3972-4842-9282-50fa5c1e3d6f" />


---

# REST API

## Books

```
GET /api/books
```

---

## Users

```
GET /api/users/students
```

---

## Assignments

```
POST /api/assignments

GET /api/assignments/teacher

GET /api/assignments/student

PATCH /api/assignments/{id}/progress
```

---

# Current Project Structure

## Backend

```text
src/main/java

assignment/
auth/
book/
common/
config/
user/
```

## Frontend

```text
src/

api/
components/
pages/
types/
```

---

# Assumptions

Because the challenge intentionally leaves several requirements open, the following assumptions were made:

- Authentication is simplified during initial development.
- Books are read-only.
- Books are seeded through Flyway.
- One assignment links one teacher, one student, and one book.
- Minutes read are manually entered by students.
- Students can only update their own assignments.
- Teachers can view all assignments they created.

---

# Work Remaining

The following features are planned before completion.

## Authentication

- JWT authentication
- Login screen
- Role-based authorization
- Route protection

---

## Backend Improvements

- Global exception handling
- Standard API error responses
- Swagger / OpenAPI
- DTO validation improvements
- Service tests
- Controller tests

---

## Frontend Improvements

- Login page
- React Router
- Protected routes
- Improved UI styling
- Responsive layouts
- Better form validation
- Loading indicators
- Error handling

---

## Deployment

- Backend deployment
- Frontend deployment
- Environment configuration

---

# Future Enhancements

If additional time were available, the following improvements would be considered:

- Classroom management
- Multiple student assignment
- Search
- Pagination
- Assignment history
- Notifications
- Reading analytics
- Teacher dashboard statistics
- Book uploads
- Rich text/PDF book viewer
- Email reminders
- Audit logging
- CI/CD pipeline
- Docker support
- Monitoring and observability

---

# Running the Project

## Backend

```bash
./mvnw spring-boot:run
```

Backend runs on

```
http://localhost:8080
```

---

## Frontend

```bash
npm install

npm run dev
```

Frontend runs on

```
http://localhost:5173
```

---

# Design Philosophy

This project intentionally prioritises:

- Clean architecture
- Readability
- Simplicity
- Extensibility
- Separation of concerns

Rather than implementing every possible feature, the goal is to produce a maintainable codebase that can evolve into a production-ready application with minimal architectural changes.

---

# Author

Dennis Njenga
