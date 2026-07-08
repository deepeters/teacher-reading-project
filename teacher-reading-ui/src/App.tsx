import { useEffect, useState } from "react";
import TeacherDashboard from "./pages/TeacherDashboard";
import StudentDashboard from "./pages/StudentDashboard";

type ViewMode = "TEACHER" | "STUDENT";
type ThemeMode = "light" | "dark";

function getInitialTheme(): ThemeMode {
  const storedTheme = localStorage.getItem("theme");

  if (storedTheme === "light" || storedTheme === "dark") {
    return storedTheme;
  }

  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
}

function App() {
  const [viewMode, setViewMode] = useState<ViewMode>("TEACHER");
  const [themeMode, setThemeMode] = useState<ThemeMode>(getInitialTheme);

  useEffect(() => {
    document.documentElement.dataset.theme = themeMode;
    localStorage.setItem("theme", themeMode);
  }, [themeMode]);

  return (
    <div className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Reading Portal</p>
          <h1>Teacher Reading Assignments</h1>
        </div>

        <div className="topbar-actions" aria-label="Application controls">
          <div className="segmented-control" aria-label="Select dashboard">
            <button
              type="button"
              className={viewMode === "TEACHER" ? "active" : ""}
              aria-pressed={viewMode === "TEACHER"}
              onClick={() => setViewMode("TEACHER")}
            >
              Teacher
            </button>
            <button
              type="button"
              className={viewMode === "STUDENT" ? "active" : ""}
              aria-pressed={viewMode === "STUDENT"}
              onClick={() => setViewMode("STUDENT")}
            >
              Student
            </button>
          </div>

          <label className="theme-switch">
            <input
              type="checkbox"
              checked={themeMode === "dark"}
              onChange={(event) => setThemeMode(event.target.checked ? "dark" : "light")}
            />
            <span>{themeMode === "dark" ? "Dark" : "Light"}</span>
          </label>
        </div>
      </header>

      {viewMode === "TEACHER" ? <TeacherDashboard /> : <StudentDashboard />}
    </div>
  );
}

export default App;
