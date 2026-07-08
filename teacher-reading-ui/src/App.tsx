import { useEffect, useState } from "react";
import { clearSession, getStoredUser, login, storeSession } from "./api/auth";
import TeacherDashboard from "./pages/TeacherDashboard";
import StudentDashboard from "./pages/StudentDashboard";
import type { User } from "./types";

type ThemeMode = "light" | "dark";

function getInitialTheme(): ThemeMode {
  const storedTheme = localStorage.getItem("theme");

  if (storedTheme === "light" || storedTheme === "dark") {
    return storedTheme;
  }

  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
}

function App() {
  const [currentUser, setCurrentUser] = useState<User | null>(getStoredUser);
  const [themeMode, setThemeMode] = useState<ThemeMode>(getInitialTheme);

  useEffect(() => {
    document.documentElement.dataset.theme = themeMode;
    localStorage.setItem("theme", themeMode);
  }, [themeMode]);

  async function handleLogin(email: string, password: string) {
    const session = await login({ email, password });
    storeSession(session);
    setCurrentUser(session.user);
  }

  function handleLogout() {
    clearSession();
    setCurrentUser(null);
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Reading Portal</p>
          <h1>Teacher Reading Assignments</h1>
        </div>

        <div className="topbar-actions" aria-label="Application controls">
          {currentUser && (
            <div className="user-pill">
              <span>{currentUser.name}</span>
              <strong>{currentUser.role.toLowerCase()}</strong>
            </div>
          )}

          <label className="theme-switch">
            <input
              type="checkbox"
              checked={themeMode === "dark"}
              onChange={(event) => setThemeMode(event.target.checked ? "dark" : "light")}
            />
            <span>{themeMode === "dark" ? "Dark" : "Light"}</span>
          </label>

          {currentUser && (
            <button type="button" className="ghost-button" onClick={handleLogout}>
              Logout
            </button>
          )}
        </div>
      </header>

      {!currentUser ? (
        <LoginPage onLogin={handleLogin} />
      ) : currentUser.role === "TEACHER" ? (
        <TeacherDashboard />
      ) : (
        <StudentDashboard />
      )}
    </div>
  );
}

type LoginPageProps = {
  onLogin: (email: string, password: string) => Promise<void>;
};

function LoginPage({ onLogin }: LoginPageProps) {
  const [email, setEmail] = useState("teacher@example.com");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      await onLogin(email, password);
    } catch {
      setError("Invalid email or password.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div>
          <p className="eyebrow">Secure access</p>
          <h2>Sign in to your reading workspace.</h2>
        </div>

        {error && <p className="alert">{error}</p>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            <span>Email</span>
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              autoComplete="email"
              required
            />
          </label>

          <label>
            <span>Password</span>
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="current-password"
              required
            />
          </label>

          <button type="submit" className="primary-button" disabled={isSubmitting}>
            {isSubmitting ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <div className="demo-logins" aria-label="Demo accounts">
          <button
            type="button"
            className="ghost-button"
            onClick={() => {
              setEmail("teacher@example.com");
              setPassword("password");
            }}
          >
            Teacher Demo
          </button>
          <button
            type="button"
            className="ghost-button"
            onClick={() => {
              setEmail("student@example.com");
              setPassword("password");
            }}
          >
            Student Demo
          </button>
        </div>
      </section>
    </main>
  );
}

export default App;
