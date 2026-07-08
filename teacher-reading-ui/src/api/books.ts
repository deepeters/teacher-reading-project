import { api } from "./client";
import type { Book } from "../types";

export async function getBooks(): Promise<Book[]> {
  const response = await api.get<Book[]>("/books");
  return response.data;
}
