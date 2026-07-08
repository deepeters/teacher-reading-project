import { api, setStoredToken } from "./client";
import type { User } from "../types";

const USER_STORAGE_KEY = "readingPortalUser";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await api.post<LoginResponse>("/auth/login", request);
  return response.data;
}

export function getStoredUser(): User | null {
  const storedUser = localStorage.getItem(USER_STORAGE_KEY);

  if (!storedUser) {
    return null;
  }

  try {
    return JSON.parse(storedUser) as User;
  } catch {
    localStorage.removeItem(USER_STORAGE_KEY);
    return null;
  }
}

export function storeSession(session: LoginResponse) {
  setStoredToken(session.token);
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(session.user));
}

export function clearSession() {
  setStoredToken(null);
  localStorage.removeItem(USER_STORAGE_KEY);
}
