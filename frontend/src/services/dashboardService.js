import api from "./api";

export async function getDashboard(period = "CURRENT_MONTH") {
  const response = await api.get("/dashboard", { params: { period } });
  return response.data;
}