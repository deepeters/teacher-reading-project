# Teacher Reading Assignment Portal - Architecture

## Overview

This is a lightweight web application that allows teachers to assign reading to students and track assignment progress.

The system supports two user roles:

* Teacher
* Student

Teachers can view available books, create reading assignments, and monitor student progress. Students can view assigned readings, open book content, track minutes read, and update assignment status.

## Tech Stack

### Backend

* Java 21
* Spring Boot 3
* Spring Web
* Spring Security
* Spring Data JPA
* PostgreSQL
* Flyway
* Lombok
* Bean Validation

### Frontend

* React
* TypeScript
* Vite
* React Router
* Axios or TanStack Query

## Architecture

The application uses a simple client-server architecture.

```text
React Frontend
    |
    | REST API
    v
Spring Boot Backend
    |
    v
PostgreSQL Database
```

The backend exposes REST endpoints consumed by the React frontend.

## Domain Model

### User

Represents both teachers and students.

Fields:

* id
* name
* email
* password
* role

Roles:

* TEACHER
* STUDENT

### Book

Represents a book that can be assigned to students.

Fields:

* id
* title
* author
* description
* content

Books are preloaded and read-only in this MVP.

### Assignment

Represents a reading assignment from a teacher to a student.

Fields:

* id
* book
* teacher
* student
* dueDate
* status
* minutesRead
* createdAt
* updatedAt

Statuses:

* NOT_STARTED
* IN_PROGRESS
* COMPLETED

## Main Features

### Authentication

Seeded demo users are used for this MVP.

The application uses role-based access control so that:

* Teachers can create and view assignments.
* Students can view and update only their own assignments.

### Teacher Features

Teachers can:

* View the list of available books.
* View students.
* Create assignments by selecting a book, student, and due date.
* View assignment progress including status and minutes read.

### Student Features

Students can:

* View assigned readings.
* Open and read assigned book content.
* Update minutes read.
* Update reading status.

## API Endpoints

### Authentication

```text
POST /api/auth/login
```

### Books

```text
GET /api/books
```

### Users

```text
GET /api/users/students
```

### Assignments

```text
POST /api/assignments
GET /api/assignments/teacher
GET /api/assignments/student
PATCH /api/assignments/{id}/progress
```

## Key Assumptions

* A teacher assigns one book to one student at a time.
* Books are seeded into the database and cannot be created through the UI.
* Assignment progress is manually updated by students.
* Minutes read are cumulative.
* Completed assignments remain visible.
* Students can only update their own assignments.
* Teachers can view all assignments they created.
* The MVP does not include notifications, comments, file uploads, search, pagination, or real-time updates.

## Tradeoffs

### Authentication

Real authentication is included, but simplified through seeded users and JWT-based login.

This keeps the project realistic without spending excessive time on registration, password reset, email verification, or account management.

### Book Management

Books are read-only and seeded through Flyway.

This keeps the focus on the core assignment workflow.

### Assignment Model

Each assignment links one teacher, one student, and one book.

This is simpler than supporting groups or classrooms, but the model can be extended later.

### Frontend Scope

The frontend focuses on the core workflows:

* Login
* Teacher dashboard
* Student dashboard
* Assignment progress updates

Advanced UI features are intentionally out of scope.

## Future Improvements

With more time, I would add:

* Classroom or group assignment support
* Assignment history and audit logs
* Search and pagination
* Rich book viewer experience
* Teacher analytics dashboard
* Email reminders before due dates
* Registration and user management
* More detailed testing coverage
* CI/CD pipeline
* Production-grade observability
* Dockerised local development
