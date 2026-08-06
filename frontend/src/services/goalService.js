import api from "./api";

export async function getGoals() {
  const response = await api.get("/goals");
  return response.data;
}

export async function createGoal(goal) {
  const response = await api.post("/goals", goal);
  return response.data;
}

export async function updateProgress(id, currentAmount) {
  const response = await api.patch(`/goals/${id}/progress`, { currentAmount });
  return response.data;
}

export async function deleteGoal(id) {
  await api.delete(`/goals/${id}`);
}