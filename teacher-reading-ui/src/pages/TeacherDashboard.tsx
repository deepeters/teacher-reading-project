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
  const inProgressCount = useMemo(
    () => assignments.filter((assignment) => assignment.status === "IN_PROGRESS").length,
    [assignments]
  );
  const totalMinutes = useMemo(
    () => assignments.reduce((total, assignment) => total + assignment.minutesRead, 0),
    [assignments]
  );
  const selectedBook = books.find((book) => String(book.id) === bookId);

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
          <div className="metric-card">
            <span className="metric-icon" aria-hidden="true">B</span>
            <span>{books.length}</span>
            <p>Books</p>
          </div>
          <div className="metric-card">
            <span className="metric-icon" aria-hidden="true">S</span>
            <span>{students.length}</span>
            <p>Students</p>
          </div>
          <div className="metric-card">
            <span className="metric-icon" aria-hidden="true">C</span>
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

          {selectedBook && (
            <div className="selected-book-preview">
              <div className="book-glyph" aria-hidden="true" />
              <div>
                <p className="eyebrow">Selected book</p>
                <h4>{selectedBook.title}</h4>
                <p>{selectedBook.author}</p>
              </div>
            </div>
          )}

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
            <div className="summary-pills" aria-label="Status summary">
              <span className="summary-pill completed">{completedCount} completed</span>
              <span className="summary-pill in-progress">{inProgressCount} active</span>
              <span className="summary-pill">{totalMinutes} minutes</span>
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
                        <StatusBadge status={assignment.status} />
                      </td>
                      <td>
                        <ProgressBar minutesRead={assignment.minutesRead} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </section>

      <section className="panel book-catalog">
        <div className="panel-header">
          <div>
            <p className="eyebrow">Library</p>
            <h3>Book Cards</h3>
          </div>
        </div>

        <div className="book-grid">
          {books.map((book) => (
            <article className={`book-card ${String(book.id) === bookId ? "selected" : ""}`} key={book.id}>
              <div className="book-card-cover" aria-hidden="true" />
              <div className="book-card-body">
                <h4>{book.title}</h4>
                <dl>
                  <div>
                    <dt>Author</dt>
                    <dd>{book.author}</dd>
                  </div>
                </dl>
                <p>{book.description}</p>
              </div>
              <button type="button" className="ghost-button" onClick={() => setBookId(String(book.id))}>
                Assign
              </button>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}

function formatStatus(status: Assignment["status"]) {
  return status.replace("_", " ").toLowerCase();
}

function statusIcon(status: Assignment["status"]) {
  if (status === "COMPLETED") {
    return "●";
  }

  if (status === "IN_PROGRESS") {
    return "●";
  }

  return "○";
}

function StatusBadge({ status }: { status: Assignment["status"] }) {
  return (
    <span className={`status-badge status-${status.toLowerCase()}`}>
      <span aria-hidden="true">{statusIcon(status)}</span>
      {formatStatus(status)}
    </span>
  );
}

function ProgressBar({ minutesRead }: { minutesRead: number }) {
  const percent = Math.min(100, Math.round((minutesRead / 100) * 100));

  return (
    <div className="progress-stack">
      <div className="progress-bar" aria-label={`${minutesRead} minutes read`}>
        <span style={{ width: `${percent}%` }} />
      </div>
      <p>{minutesRead} minutes</p>
    </div>
  );
}
