CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       description VARCHAR(1000) NOT NULL,
                       content TEXT NOT NULL
);

CREATE TABLE assignments (
                             id BIGSERIAL PRIMARY KEY,
                             book_id BIGINT NOT NULL,
                             teacher_id BIGINT NOT NULL,
                             student_id BIGINT NOT NULL,
                             due_date DATE NOT NULL,
                             status VARCHAR(50) NOT NULL,
                             minutes_read INTEGER NOT NULL DEFAULT 0,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,

                             CONSTRAINT fk_assignment_book
                                 FOREIGN KEY (book_id) REFERENCES books(id),

                             CONSTRAINT fk_assignment_teacher
                                 FOREIGN KEY (teacher_id) REFERENCES users(id),

                             CONSTRAINT fk_assignment_student
                                 FOREIGN KEY (student_id) REFERENCES users(id)
);