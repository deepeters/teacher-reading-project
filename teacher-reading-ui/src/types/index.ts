export type UserRole = "TEACHER" | "STUDENT";

export type AssignmentStatus = "NOT_STARTED" | "IN_PROGRESS" | "COMPLETED";

export interface Book {
  id: number;
  title: string;
  author: string;
  description: string;
  content: string;
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: UserRole;
}

export interface Assignment {
  id: number;
  bookId: number;
  bookTitle: string;
  bookAuthor: string;
  bookContent: string;
  teacherId: number;
  teacherName: string;
  studentId: number;
  studentName: string;
  dueDate: string;
  status: AssignmentStatus;
  minutesRead: number;
  createdAt: string;
  updatedAt: string;
}
