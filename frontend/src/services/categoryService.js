import api from "./api";

export async function getCategories() {
  const response = await api.get("/categories");
  return response.data;
}

export async function createCategory(name, type) {
  const response = await api.post("/categories", { name, type });
  return response.data;
}

export async function deleteCategory(id) {
  await api.delete(`/categories/${id}`);
}