import { useState, useEffect } from "react";
import {
  getGoals,
  createGoal,
  updateProgress,
  deleteGoal,
} from "../services/goalService";
import Input from "../components/Input";
import ConfirmDialog from "../components/ConfirmDialog";
import CurrencyInput from "../components/CurrencyInput";

function formatCurrency(value) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value);
}

function GoalCard({ goal, onProgressUpdate, onDeleteRequest }) {
  const [newAmount, setNewAmount] = useState(goal.currentAmount);

  async function handleProgressSubmit(event) {
    event.preventDefault();
    onProgressUpdate(goal.id, newAmount);
  }

  const progress = Math.min(goal.progressPercentage, 100);

  return (
    <div className="bg-surface rounded-2xl shadow-md p-6 flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <h3 className="text-text-primary font-medium">{goal.title}</h3>
        <button
          onClick={() => onDeleteRequest(goal)}
          className="text-text-secondary hover:text-expense transition text-sm"
        >
          Excluir
        </button>
      </div>

      <div className="w-full bg-neutral rounded-full h-2">
        <div
          className="bg-goals h-2 rounded-full transition-all"
          style={{ width: `${progress}%` }}
        />
      </div>

      <div className="flex justify-between text-sm text-text-secondary">
        <span>{formatCurrency(goal.currentAmount)}</span>
        <span>{formatCurrency(goal.targetAmount)}</span>
      </div>

      <p className="text-sm text-text-secondary">
        {goal.progressPercentage.toFixed(1)}% concluído
        {goal.targetDate && ` • até ${goal.targetDate}`}
      </p>

      <form onSubmit={handleProgressSubmit} className="flex flex-col gap-2 mt-2">
        <CurrencyInput label="" value={newAmount} onChange={setNewAmount} />
        <button
            type="submit"
            className="bg-primary text-white rounded-lg px-4 py-1 text-sm hover:opacity-90 transition w-full"
         >
            Atualizar
         </button>
      </form>
    </div>
  );
}

function Goals() {
  const [goals, setGoals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [title, setTitle] = useState("");
  const [targetAmount, setTargetAmount] = useState(0);
  const [targetDate, setTargetDate] = useState("");

  const [goalToDelete, setGoalToDelete] = useState(null);

  useEffect(() => {
    loadGoals();
  }, []);

  async function loadGoals() {
    try {
      const data = await getGoals();
      setGoals(data);
    } catch (err) {
      setError("Não foi possível carregar as metas.");
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    try {
      await createGoal({
        title,
        targetAmount,
        targetDate: targetDate || null,
      });
      setTitle("");
      setTargetAmount(0);
      setTargetDate("");
      loadGoals();
    } catch (err) {
      setError("Não foi possível criar a meta. Verifique os dados informados.");
    }
  }

  async function handleProgressUpdate(id, currentAmount) {
    try {
      await updateProgress(id, currentAmount);
      loadGoals();
    } catch (err) {
      setError("Não foi possível atualizar o progresso.");
    }
  }

  async function confirmDelete() {
    try {
      await deleteGoal(goalToDelete.id);
      setGoalToDelete(null);
      loadGoals();
    } catch (err) {
      setError("Não foi possível excluir a meta.");
      setGoalToDelete(null);
    }
  }

  return (
    <div className="p-6">
      <h1 className="text-2xl font-sans text-text-primary mb-6">Metas</h1>

      <form
        onSubmit={handleSubmit}
        className="bg-surface rounded-2xl shadow-md p-6 mb-6 grid grid-cols-1 sm:grid-cols-3 gap-4 items-end"
      >
        <Input label="Nome" value={title} onChange={(e) => setTitle(e.target.value)} />

        <CurrencyInput label="Valor alvo" value={targetAmount} onChange={setTargetAmount} />

        <Input
          label="Data prevista (opcional)"
          type="date"
          value={targetDate}
          onChange={(e) => setTargetDate(e.target.value)}
        />

        <button
          type="submit"
          className="bg-primary text-white rounded-lg py-2 hover:opacity-90 transition sm:col-span-3"
        >
          Criar meta
        </button>
      </form>

      {error && <p className="text-sm text-expense mb-4">{error}</p>}

{loading ? (
  <p className="text-text-secondary">Carregando...</p>
) : goals.length === 0 ? (
  <div className="bg-surface rounded-2xl shadow-md p-8 text-center">
    <p className="text-text-secondary">
      Nenhuma meta criada ainda. Que tal começar uma agora?
    </p>
  </div>
) : (
  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
    {goals.map((goal) => (
      <GoalCard
        key={goal.id}
        goal={goal}
        onProgressUpdate={handleProgressUpdate}
        onDeleteRequest={setGoalToDelete}
      />
    ))}
  </div>
)}

      <ConfirmDialog
        isOpen={!!goalToDelete}
        title="Excluir meta"
        message={`Tem certeza que deseja excluir "${goalToDelete?.title}"? Essa ação não pode ser desfeita.`}
        onConfirm={confirmDelete}
        onCancel={() => setGoalToDelete(null)}
      />
    </div>
  );
}

export default Goals;