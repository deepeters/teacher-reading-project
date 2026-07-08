UPDATE users
SET password = '$2a$10$EQklQ3UHbxnR2eE4jFfAQeUt0OUjZEYbye.8GKs6cS7O3zIXeWLmu'
WHERE email IN ('teacher@example.com', 'student@example.com', 'alice@example.com');
