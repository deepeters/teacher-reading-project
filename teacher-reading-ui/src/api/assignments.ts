import { api } from "./client";
import type { Assignment, AssignmentStatus } from "../types";

export interface CreateAssignmentRequest {
  bookId: number;
  studentId: number;
  dueDate: string;
}

export interface UpdateProgressRequest {
  status: AssignmentStatus;
  minutesRead: number;
}

export async function createAssignment(
  request: CreateAssignmentRequest
): Promise<Assignment> {
  const response = await api.post<Assignment>("/assignments", request);
  return response.data;
}

export async function getTeacherAssignments(): Promise<Assignment[]> {
  const response = await api.get<Assignment[]>("/assignments/teacher");
  return response.data;
}

export async function getStudentAssignments(): Promise<Assignment[]> {
  const response = await api.get<Assignment[]>("/assignments/student");
  return response.data;
}

export async function updateAssignmentProgress(
  assignmentId: number,
  request: UpdateProgressRequest
): Promise<Assignment> {
  const response = await api.patch<Assignment>(
    `/assignments/${assignmentId}/progress`,
    request
  );

  return response.data;
}
