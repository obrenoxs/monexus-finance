import { useState, useEffect } from "react";
import {
  getTransactions,
  createTransaction,
  deleteTransaction,
} from "../services/transactionService";
import { getCategories } from "../services/categoryService";
import Input from "../components/Input";
import ConfirmDialog from "../components/ConfirmDialog";

function formatCurrency(value) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value);
}

function Transactions() {
  const [transactions, setTransactions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [date, setDate] = useState("");
  const [type, setType] = useState("EXPENSE");
  const [categoryId, setCategoryId] = useState("");

  const [transactionToDelete, setTransactionToDelete] = useState(null);

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      const [transactionsData, categoriesData] = await Promise.all([
        getTransactions(),
        getCategories(),
      ]);
      setTransactions(transactionsData);
      setCategories(categoriesData);
    } catch (err) {
      setError("Não foi possível carregar os dados.");
    } finally {
      setLoading(false);
    }
  }

  const filteredCategories = categories.filter((c) => c.type === type);

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    if (!categoryId) {
      setError("Selecione uma categoria.");
      return;
    }

    try {
      await createTransaction({
        description,
        amount: parseFloat(amount),
        date,
        type,
        categoryId: Number(categoryId),
      });
      setDescription("");
      setAmount("");
      setDate("");
      setCategoryId("");
      loadData();
    } catch (err) {
      setError("Não foi possível criar a transação. Verifique os dados informados.");
    }
  }

  async function confirmDelete() {
    try {
      await deleteTransaction(transactionToDelete.id);
      setTransactionToDelete(null);
      loadData();
    } catch (err) {
      setError("Não foi possível excluir a transação.");
      setTransactionToDelete(null);
    }
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-sans text-text-primary mb-6">Transações</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-surface rounded-2xl shadow-md p-6 mb-6 flex flex-col gap-4"
      >
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="Descrição"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
          <Input
            label="Valor"
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
          <Input
            label="Data"
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
          />

          <div className="flex flex-col gap-1 text-left">
            <label className="text-sm text-text-secondary font-sans">Tipo</label>
            <select
              value={type}
              onChange={(e) => {
                setType(e.target.value);
                setCategoryId("");
              }}
              className="rounded-lg border border-neutral px-4 py-2 text-text-primary"
            >
              <option value="EXPENSE">Despesa</option>
              <option value="INCOME">Receita</option>
            </select>
          </div>

          <div className="flex flex-col gap-1 text-left sm:col-span-2">
            <label className="text-sm text-text-secondary font-sans">Categoria</label>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
              className="rounded-lg border border-neutral px-4 py-2 text-text-primary"
            >
              <option value="">Selecione uma categoria</option>
              {filteredCategories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>
        </div>

        <button
          type="submit"
          className="bg-primary text-white rounded-lg py-2 hover:opacity-90 transition self-start px-6"
        >
          Adicionar
        </button>
      </form>

      {error && <p className="text-sm text-expense mb-4">{error}</p>}

      {loading ? (
        <p className="text-text-secondary">Carregando...</p>
      ) : (
        <div className="flex flex-col gap-3">
          {transactions.map((transaction) => (
            <div
              key={transaction.id}
              className="bg-surface rounded-2xl shadow-md p-4 flex items-center justify-between"
            >
              <div>
                <p className="text-text-primary font-medium">{transaction.description}</p>
                <p className="text-sm text-text-secondary">
                  {transaction.categoryName} • {transaction.date}
                </p>
              </div>

              <div className="flex items-center gap-4">
                <p
                  className={`font-mono ${
                    transaction.type === "INCOME" ? "text-success" : "text-expense"
                  }`}
                >
                  {transaction.type === "INCOME" ? "+" : "-"}
                  {formatCurrency(transaction.amount)}
                </p>
                <button
                  onClick={() => setTransactionToDelete(transaction)}
                  className="text-text-secondary hover:text-expense transition"
                >
                  Excluir
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <ConfirmDialog
        isOpen={!!transactionToDelete}
        title="Excluir transação"
        message={`Tem certeza que deseja excluir "${transactionToDelete?.description}"? Essa ação não pode ser desfeita.`}
        onConfirm={confirmDelete}
        onCancel={() => setTransactionToDelete(null)}
      />
    </div>
  );
}

export default Transactions;