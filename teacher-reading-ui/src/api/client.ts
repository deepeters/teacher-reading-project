import axios from "axios";

export const TOKEN_STORAGE_KEY = "readingPortalToken";

export const api = axios.create({
  // baseURL: "http://localhost:8080/api",
  baseURL: "https://teacher-reading-backend-2.onrender.com/api",
});

export function getStoredToken() {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

export function setStoredToken(token: string | null) {
  if (token) {
    localStorage.setItem(TOKEN_STORAGE_KEY, token);
  } else {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
  }
}

api.interceptors.request.use((config) => {
  const token = getStoredToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});
