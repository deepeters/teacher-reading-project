import { useEffect, useMemo, useState } from "react";
import { createAssignment, getTeacherAssignments } from "../api/assignments";
import { getBooks } from "../api/books";
import { getStudents } from "../api/users";
import type { Assignment, Book, User } from "../types";

export default function TeacherDashboard() {
  const [books, setBooks] = useState<Book[]>([]);
  const [students, setStudents] = useState<User[]>([]);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const [bookId, setBookId] = useState("");
  const [studentId, setStudentId] = useState("");
  const [dueDate, setDueDate] = useState("");

  const completedCount = useMemo(
    () => assignments.filter((assignment) => assignment.status === "COMPLETED").length,
    [assignments]
  );

  async function loadData() {
    setError("");

    try {
      const [booksData, studentsData, assignmentsData] = await Promise.all([
        getBooks(),
        getStudents(),
        getTeacherAssignments(),
      ]);

      setBooks(booksData);
      setStudents(studentsData);
      setAssignments(assignmentsData);
    } catch {
      setError("Unable to load dashboard data. Check that the backend is running.");
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setIsSubmitting(true);
    setError("");

    try {
      await createAssignment({
        bookId: Number(bookId),
        studentId: Number(studentId),
        dueDate,
      });

      setBookId("");
      setStudentId("");
      setDueDate("");

      await loadData();
    } catch {
      setError("Unable to create the assignment. Review the selections and due date.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="page">
      <section className="page-heading">
        <div>
          <p className="eyebrow">Teacher workspace</p>
          <h2>Assign books and monitor reading progress.</h2>
        </div>

        <div className="metric-strip" aria-label="Assignment summary">
          <div>
            <span>{books.length}</span>
            <p>Books</p>
          </div>
          <div>
            <span>{students.length}</span>
            <p>Students</p>
          </div>
          <div>
            <span>{completedCount}/{assignments.length}</span>
            <p>Completed</p>
          </div>
        </div>
      </section>

      {error && <p className="alert">{error}</p>}

      <section className="dashboard-grid">
        <form className="panel assignment-form" onSubmit={handleSubmit}>
          <div className="panel-header">
            <div>
              <p className="eyebrow">New assignment</p>
              <h3>Create Reading Assignment</h3>
            </div>
          </div>

          <label>
            <span>Book</span>
            <select value={bookId} onChange={(e) => setBookId(e.target.value)} required>
              <option value="">Select book</option>
              {books.map((book) => (
                <option key={book.id} value={book.id}>
                  {book.title} by {book.author}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Student</span>
            <select
              value={studentId}
              onChange={(e) => setStudentId(e.target.value)}
              required
            >
              <option value="">Select student</option>
              {students.map((student) => (
                <option key={student.id} value={student.id}>
                  {student.name}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span>Due date</span>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              required
            />
          </label>

          <button type="submit" className="primary-button" disabled={isSubmitting}>
            {isSubmitting ? "Creating..." : "Create Assignment"}
          </button>
        </form>

        <section className="panel table-panel">
          <div className="panel-header">
            <div>
              <p className="eyebrow">Progress</p>
              <h3>Created Assignments</h3>
            </div>
            <button type="button" className="ghost-button" onClick={loadData}>
              Refresh
            </button>
          </div>

          {isLoading ? (
            <p className="empty-state">Loading assignments...</p>
          ) : assignments.length === 0 ? (
            <p className="empty-state">No assignments have been created yet.</p>
          ) : (
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Student</th>
                    <th>Book</th>
                    <th>Due Date</th>
                    <th>Status</th>
                    <th>Minutes</th>
                  </tr>
                </thead>
                <tbody>
                  {assignments.map((assignment) => (
                    <tr key={assignment.id}>
                      <td>{assignment.studentName}</td>
                      <td>{assignment.bookTitle}</td>
                      <td>{assignment.dueDate}</td>
                      <td>
                        <span className={`status-badge status-${assignment.status.toLowerCase()}`}>
                          {formatStatus(assignment.status)}
                        </span>
                      </td>
                      <td>{assignment.minutesRead}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </section>
    </main>
  );
}

function formatStatus(status: Assignment["status"]) {
  return status.replace("_", " ").toLowerCase();
}
