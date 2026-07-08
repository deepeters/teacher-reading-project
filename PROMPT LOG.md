# Prompt Log

This document captures the prompts used to build the Teacher Reading Assignment Portal, rewritten in a more deliberate senior-engineering style for discussion during review or interview.

The prompts are organized chronologically by implementation phase.

## 1. Project Context And Architecture

### Prompt

Read `ARCHITECTURE.md` and load it into context before making implementation decisions. Then initialize a React + TypeScript frontend project in `teacher-reading-ui`, keeping the frontend structure compatible with the backend architecture described in the document.

### Intent

Establish shared architectural context before implementation and create a clean frontend workspace.

## 2. Requirements Context

### Prompt

Load the project instructions document into context as well. Treat it as the source of truth for the product goal, expected behavior, and MVP scope. Use both the architecture document and the instructions to guide implementation choices.

### Intent

Ensure that implementation decisions are grounded in the assignment requirements, not just code structure.

## 3. Backend Package Structure And Domain Model

### Prompt

Create a backend package structure under the main application package with clear domain boundaries:

```text
assignment
auth
book
common
config
user
```

Then implement the core enums, JPA entities, and repositories for users, books, and reading assignments. Use idiomatic Spring Data JPA, Jakarta Persistence annotations, Lombok, and lifecycle callbacks for assignment timestamps.

### Intent

Establish the backend domain model and persistence layer in a maintainable package structure.

## 4. Book DTO And Read Endpoint

### Prompt

Add a `BookResponse` DTO, `BookService`, and `BookController` to expose `GET /api/books`. Return all seeded books as response DTOs rather than exposing entities directly. Temporarily permit the endpoint through Spring Security so the API can be verified before full authentication is implemented.

### Intent

Create the first working API endpoint and introduce the DTO/service/controller pattern.

## 5. Student Read Endpoint

### Prompt

Add a `UserResponse` DTO, `UserService`, and `UserController` endpoint for `GET /api/users/students`. The endpoint should return only users with the `STUDENT` role and should avoid exposing passwords or entity internals.

### Intent

Support assignment creation by allowing the teacher UI to list available students.

## 6. Assignment Creation API

### Prompt

Implement assignment creation through `POST /api/assignments`. Add request and response DTOs, a domain-level `NotFoundException`, service logic to validate that the selected user is a student, and a controller endpoint using Bean Validation. Use a temporary demo teacher lookup until authentication is implemented.

### Intent

Deliver the primary teacher workflow while preserving a clear path to replacing demo-user lookup with authenticated-user lookup.

## 7. Assignment Read APIs

### Prompt

Add read endpoints for teacher and student assignment views:

```http
GET /api/assignments/teacher
GET /api/assignments/student
```

For now, resolve the demo teacher and demo student by email in the service layer. Return `AssignmentResponse` DTOs and keep the implementation ready to switch to the authenticated current user later.

### Intent

Enable both teacher and student dashboards to retrieve role-specific assignment data.

## 8. Assignment Progress Updates

### Prompt

Add a progress update endpoint:

```http
PATCH /api/assignments/{id}/progress
```

Create an `UpdateAssignmentProgressRequest` DTO with validation for status and non-negative minutes. Update assignment status and minutes read in the service, relying on the entity `@PreUpdate` callback for the `updatedAt` timestamp.

### Intent

Complete the student-side MVP workflow for tracking reading progress.

## 9. API Hardening And Package Cleanup

### Prompt

Polish the backend API by adding global exception handling with `@RestControllerAdvice`, consistent JSON error responses, clean Bean Validation error payloads, Swagger/OpenAPI documentation, and package cleanup so controllers, services, and DTOs are clearly separated by domain.

### Intent

Improve maintainability, error consistency, and API usability before expanding the application.

## 10. Service Unit Tests

### Prompt

Add focused unit tests for the service layer we have built so far. Cover the successful paths and important failure paths for book listing, student listing, assignment creation, assignment reads, and assignment progress updates. Use Mockito to isolate repositories and verify service behavior.

### Intent

Protect the core business logic before adding more application features.

## 11. Frontend API Layer

### Prompt

Create a frontend API structure with an Axios base client, shared TypeScript domain types, and API modules for books, users, and assignments. The API layer should map directly to the backend REST endpoints and keep request/response types explicit.

### Intent

Create a clean boundary between React components and backend HTTP calls.

## 12. Teacher Dashboard

### Prompt

Build a `TeacherDashboard` page that loads books, students, and teacher assignments concurrently. Provide a form for creating assignments and a table for reviewing created assignments. Keep the initial implementation simple and functional, then wire it into `App.tsx`.

### Intent

Deliver the teacher workflow end to end from the React UI.

## 13. Student Dashboard

### Prompt

Build a `StudentDashboard` page that loads student assignments, displays assigned reading, lets the student open a book, and supports updating status and minutes read. Add a simple role switcher in `App.tsx` while authentication is still pending.

### Intent

Deliver the student workflow end to end from the React UI.

## 14. Book Content In Assignment Responses

### Prompt

Update `AssignmentResponse` to include `bookContent` so students can read assigned book content directly from the student dashboard. Update the frontend assignment type and replace the placeholder book viewer with a real content viewer that renders the seeded book text.

### Intent

Make the reading experience functional instead of purely metadata-based.

## 15. Visual UI Improvement And Theme Support

### Prompt

Improve the frontend UI to feel more polished and production-ready. Add both light and dark modes, persisted theme preference, a stronger app shell, better dashboard layouts, styled panels, loading and error states, and responsive behavior across teacher and student views.

### Intent

Move the UI from a functional prototype to a more presentable application.

## 16. JWT Authentication Flow

### Prompt

Replace hardcoded demo-user lookups with real authentication:

```text
POST /api/auth/login
JWT returned
React stores token
Axios attaches bearer token
Spring Security validates JWT
Services resolve the authenticated current user
```

Add login DTOs, authentication service/controller, JWT generation and validation, a security filter, password verification, and an authentication facade. Refactor assignment services to use `authenticationFacade.getCurrentUser()` instead of hardcoded email lookup.

### Intent

Turn the MVP into a more realistic role-aware application with authenticated backend behavior.

## 17. Frontend Authentication Integration

### Prompt

Update the frontend to support the JWT flow. Add a login screen, token persistence, Axios request interceptor, logout handling, and role-based dashboard rendering based on the authenticated user returned by login. Remove the temporary manual role switcher.

### Intent

Align the frontend with the backend security model and remove demo-only behavior.

## 18. UI Polish Phase

### Prompt

Polish the dashboards so the submission stands out visually. Add dashboard metric cards, expressive status chips, progress bars for reading minutes, book cards with assign actions, selected-book previews, and improved student assignment cards. Preserve existing behavior while improving presentation.

### Intent

Improve perceived quality with focused UI enhancements that are low risk and high impact.

## 19. Pre-Deployment Review

### Prompt

Before deployment and submission, review the application and identify the highest-impact improvements. Prioritize changes that improve deployment readiness, authentication quality, documentation, API usability, testing, and demo presentation.

### Intent

Create a pragmatic final-hardening plan before submission.

## 20. Expanded Seed Data

### Prompt

Improve demo data by adding richer seeded content. Expand the existing story beginning with Lina in `The Curious Forest` into a three-paragraph story, enrich the other seeded books, add more demo students, add more books, and seed assignments across all statuses. Use a new Flyway migration rather than editing already-applied migrations.

### Intent

Make the demo dashboards look populated and realistic immediately after setup.

## 21. Swagger JWT Support

### Prompt

Add Swagger/OpenAPI JWT bearer support so protected endpoints can be tested directly from Swagger UI. Define a `bearerAuth` HTTP security scheme, apply it globally, and mark the login endpoint as unauthenticated in the generated documentation.

### Intent

Improve API review and manual testing experience.

## 22. Submission README

### Prompt

Create a clean submission README that includes project overview, backend setup, frontend setup, demo credentials, API endpoints, Swagger JWT usage, screenshots, seed data notes, deployment notes, verification commands, and known future improvements.

### Intent

Provide reviewers with a complete, self-contained guide to running and evaluating the application.

## 23. Auth And Security Integration Tests

### Prompt

Add API-level integration tests for the authentication and authorization behavior. Cover valid login, failed login, unauthorized access without a token, a teacher attempting to call a student-only progress endpoint, and a student attempting to create an assignment. These tests should exercise Spring Security, JWT validation, controllers, and the seeded database together.

### Intent

Validate the security model at the HTTP boundary rather than only testing service methods.

## 24. Prompt Log For Interview Discussion

### Prompt

Create a `PROMPT LOG.md` file that documents the prompts used to build the application. Rewrite them in a clear, senior-engineering style so they can be discussed during an interview as examples of iterative product and engineering direction.

### Intent

Capture the build process as a coherent technical narrative for interview discussion.

## Summary

The prompting strategy followed a deliberate sequence:

1. Establish context and architecture.
2. Build the backend domain model.
3. Add API endpoints incrementally.
4. Add DTOs, validation, exceptions, and documentation.
5. Build the frontend API layer and dashboards.
6. Replace demo assumptions with JWT authentication.
7. Polish the UI and seed data for presentation.
8. Add documentation and integration tests for submission readiness.

This sequence kept the application moving from a working MVP toward a more realistic, reviewable full-stack system.
