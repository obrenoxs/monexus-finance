import { useState, useEffect } from "react";
import { getCategories, createCategory, deleteCategory } from "../services/categoryService";
import Input from "../components/Input";
import ConfirmDialog from "../components/ConfirmDialog";

function Categories() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [name, setName] = useState("");
  const [type, setType] = useState("EXPENSE");
  const [error, setError] = useState("");
  const [categoryToDelete, setCategoryToDelete] = useState(null);

  useEffect(() => {
    loadCategories();
  }, []);

  async function loadCategories() {
    try {
      const data = await getCategories();
      setCategories(data);
    } catch (err) {
      setError("Não foi possível carregar as categorias.");
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    try {
      await createCategory(name, type);
      setName("");
      loadCategories();
    } catch (err) {
      setError("Não foi possível criar a categoria. Verifique se ela já existe.");
    }
  }

function requestDelete(category) {
  setCategoryToDelete(category);
}

async function confirmDelete() {
  try {
    await deleteCategory(categoryToDelete.id);
    setCategoryToDelete(null);
    loadCategories();
  } catch (err) {
    setError("Não foi possível excluir. Verifique se há transações vinculadas.");
    setCategoryToDelete(null);
  }
}

  return (
    <div className="p-6">
      <h1 className="text-2xl font-sans text-text-primary mb-6">Categorias</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-surface rounded-2xl shadow-md p-6 mb-6 flex flex-col sm:flex-row gap-4 items-end"
      >
        <div className="flex-1 w-full">
          <Input label="Nome" value={name} onChange={(e) => setName(e.target.value)} />
        </div>

        <div className="flex flex-col gap-1 text-left w-full sm:w-40">
          <label className="text-sm text-text-secondary font-sans">Tipo</label>
          <select
            value={type}
            onChange={(e) => setType(e.target.value)}
            className="rounded-lg border border-neutral px-4 py-2 text-text-primary"
          >
            <option value="EXPENSE">Despesa</option>
            <option value="INCOME">Receita</option>
          </select>
        </div>

        <button
          type="submit"
          className="bg-primary text-white rounded-lg px-6 py-2 hover:opacity-90 transition whitespace-nowrap"
        >
          Adicionar
        </button>
      </form>

      {error && <p className="text-sm text-expense mb-4">{error}</p>}

      {loading ? (
        <p className="text-text-secondary">Carregando...</p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {categories.map((category) => (
            <div
              key={category.id}
              className="bg-surface rounded-2xl shadow-md p-4 flex items-center justify-between"
            >
              <div>
                <p className="text-text-primary font-medium">{category.name}</p>
                <span
                  className={`text-xs px-2 py-1 rounded-full ${
                    category.type === "INCOME"
                      ? "bg-success/10 text-success"
                      : "bg-expense/10 text-expense"
                  }`}
                >
                  {category.type === "INCOME" ? "Receita" : "Despesa"}
                </span>
              </div>

                <button
                    onClick={() => requestDelete(category)}
                    className="text-text-secondary hover:text-expense transition"
                    >
                    Excluir
                </button>
            </div>
          ))}
        </div>
      )}
      <ConfirmDialog
                isOpen={!!categoryToDelete}
                title="Excluir categoria"
                message={`Tem certeza que deseja excluir "${categoryToDelete?.name}"? Essa ação não pode ser desfeita.`}
                onConfirm={confirmDelete}
                onCancel={() => setCategoryToDelete(null)}
            />
    </div>
  );
}

export default Categories;