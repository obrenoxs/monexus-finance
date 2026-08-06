import api from "./api";

export async function getTransactions() {
  const response = await api.get("/transactions");
  return response.data;
}

export async function createTransaction(transaction) {
  const response = await api.post("/transactions", transaction);
  return response.data;
}

export async function deleteTransaction(id) {
  await api.delete(`/transactions/${id}`);
}