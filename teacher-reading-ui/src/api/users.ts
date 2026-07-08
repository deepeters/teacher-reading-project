import { api } from "./client";
import type { User } from "../types";

export async function getStudents(): Promise<User[]> {
  const response = await api.get<User[]>("/users/students");
  return response.data;
}
