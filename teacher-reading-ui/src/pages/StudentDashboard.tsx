import { useEffect, useMemo, useState } from "react";
import {
  getStudentAssignments,
  updateAssignmentProgress,
} from "../api/assignments";
import type { Assignment, AssignmentStatus } from "../types";

export default function StudentDashboard() {
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [selectedAssignment, setSelectedAssignment] =
    useState<Assignment | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const completedCount = useMemo(
    () => assignments.filter((assignment) => assignment.status === "COMPLETED").length,
    [assignments]
  );
  const totalMinutes = useMemo(
    () => assignments.reduce((total, assignment) => total + assignment.minutesRead, 0),
    [assignments]
  );
  const activeCount = useMemo(
    () => assignments.filter((assignment) => assignment.status === "IN_PROGRESS").length,
    [assignments]
  );

  async function loadAssignments() {
    setError("");

    try {
      const data = await getStudentAssignments();
      setAssignments(data);
      setSelectedAssignment((current) => {
        if (!current) {
          return current;
        }

        return data.find((assignment) => assignment.id === current.id) ?? current;
      });
    } catch {
      setError("Unable to load assignments. Check that the backend is running.");
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadAssignments();
  }, []);

  async function handleProgressUpdate(
    assignmentId: number,
    status: AssignmentStatus,
    minutesRead: number
  ) {
    setError("");

    try {
      const updatedAssignment = await updateAssignmentProgress(assignmentId, {
        status,
        minutesRead,
      });

      setSelectedAssignment((current) =>
        current?.id === updatedAssignment.id ? updatedAssignment : current
      );
      await loadAssignments();
    } catch {
      setError("Unable to save progress. Check the status and minutes read.");
    }
  }

  return (
    <main className="page">
      <section className="page-heading">
        <div>
          <p className="eyebrow">Student workspace</p>
          <h2>Read assigned books and keep your progress current.</h2>
        </div>
        <div className="metric-strip" aria-label="Reading summary">
          <div className="metric-card">
            <span className="metric-icon" aria-hidden="true">A</span>
            <span>{assignments.length}</span>
            <p>Assigned</p>
          </div>
          <div className="metric-card">
            <span className="metric-icon" aria-hidden="true">C</span>
            <span>{completedCount}</span>
            <p>Completed</p>
          </div>
          <div className="metric-card">
            <span className="metric-icon" aria-hidden="true">M</span>
            <span>{totalMinutes}</span>
            <p>Minutes</p>
          </div>
        </div>
      </section>

      {error && <p className="alert">{error}</p>}

      <section className="reader-layout">
        <section className="panel">
          <div className="panel-header">
            <div>
              <p className="eyebrow">Reading list</p>
              <h3>Assigned Reading</h3>
            </div>
            <div className="summary-pills" aria-label="Reading status summary">
              <span className="summary-pill in-progress">{activeCount} active</span>
              <span className="summary-pill completed">{completedCount} done</span>
            </div>
            <button type="button" className="ghost-button" onClick={loadAssignments}>
              Refresh
            </button>
          </div>

          {isLoading ? (
            <p className="empty-state">Loading assignments...</p>
          ) : assignments.length === 0 ? (
            <p className="empty-state">No assignments yet.</p>
          ) : (
            <div className="assignment-list">
              {assignments.map((assignment) => (
                <AssignmentCard
                  key={assignment.id}
                  assignment={assignment}
                  isSelected={selectedAssignment?.id === assignment.id}
                  onOpen={() => setSelectedAssignment(assignment)}
                  onUpdate={handleProgressUpdate}
                />
              ))}
            </div>
          )}
        </section>

        {selectedAssignment && (
          <section className="panel reader-panel">
            <div className="reader-heading">
              <div>
                <p className="eyebrow">Book viewer</p>
                <h3>{selectedAssignment.bookTitle}</h3>
              </div>
              <button
                type="button"
                className="ghost-button"
                onClick={() => setSelectedAssignment(null)}
              >
                Close
              </button>
            </div>

            <dl className="book-meta">
              <div>
                <dt>Author</dt>
                <dd>{selectedAssignment.bookAuthor}</dd>
              </div>
              <div>
                <dt>Due Date</dt>
                <dd>{selectedAssignment.dueDate}</dd>
              </div>
              <div>
                <dt>Status</dt>
                <dd><StatusBadge status={selectedAssignment.status} /></dd>
              </div>
              <div>
                <dt>Progress</dt>
                <dd><ProgressBar minutesRead={selectedAssignment.minutesRead} /></dd>
              </div>
            </dl>

            <article className="book-content">{selectedAssignment.bookContent}</article>
          </section>
        )}
      </section>
    </main>
  );
}

type AssignmentCardProps = {
  assignment: Assignment;
  isSelected: boolean;
  onOpen: () => void;
  onUpdate: (
    assignmentId: number,
    status: AssignmentStatus,
    minutesRead: number
  ) => Promise<void>;
};

function AssignmentCard({
  assignment,
  isSelected,
  onOpen,
  onUpdate,
}: AssignmentCardProps) {
  const [status, setStatus] = useState<AssignmentStatus>(assignment.status);
  const [minutesRead, setMinutesRead] = useState(assignment.minutesRead);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    setStatus(assignment.status);
    setMinutesRead(assignment.minutesRead);
  }, [assignment.status, assignment.minutesRead]);

  async function saveProgress() {
    setIsSaving(true);

    try {
      await onUpdate(assignment.id, status, minutesRead);
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <article className={`assignment-card ${isSelected ? "selected" : ""}`}>
      <div className="assignment-card-header">
        <div className="assignment-title-group">
          <div className="book-glyph" aria-hidden="true" />
          <div>
            <h4>{assignment.bookTitle}</h4>
            <p>Due {assignment.dueDate}</p>
          </div>
        </div>
        <StatusBadge status={assignment.status} />
      </div>

      <div className="assignment-card-stats">
        <ProgressBar minutesRead={assignment.minutesRead} />
        <button type="button" className="ghost-button" onClick={onOpen}>
          Open Book
        </button>
      </div>

      <div className="progress-editor">
        <label>
          <span>Status</span>
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value as AssignmentStatus)}
          >
            <option value="NOT_STARTED">Not Started</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </label>

        <label>
          <span>Minutes read</span>
          <input
            type="number"
            min={0}
            value={minutesRead}
            onChange={(e) => setMinutesRead(Number(e.target.value))}
          />
        </label>

        <button type="button" className="primary-button" onClick={saveProgress} disabled={isSaving}>
          {isSaving ? "Saving..." : "Save Progress"}
        </button>
      </div>
    </article>
  );
}

function formatStatus(status: AssignmentStatus) {
  return status.replace("_", " ").toLowerCase();
}

function statusIcon(status: AssignmentStatus) {
  if (status === "COMPLETED") {
    return "●";
  }

  if (status === "IN_PROGRESS") {
    return "●";
  }

  return "○";
}

function StatusBadge({ status }: { status: AssignmentStatus }) {
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
