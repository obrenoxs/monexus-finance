import { useState, useEffect } from "react";
import { getDashboard } from "../services/dashboardService";

function formatCurrency(value) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value);
}

function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDashboard() {
      try {
        const result = await getDashboard();
        setData(result);
      } catch (err) {
        setError("Não foi possível carregar o dashboard.");
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <p className="text-text-secondary">Carregando...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <p className="text-expense">{error}</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6">
      <h1 className="text-2xl font-sans text-text-primary mb-6">
        Dashboard
      </h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-surface rounded-2xl shadow-md p-6">
          <p className="text-sm text-text-secondary mb-1">Saldo Atual</p>
          <p className="text-xl font-mono text-primary">
            {formatCurrency(data.currentBalance)}
          </p>
        </div>

        <div className="bg-surface rounded-2xl shadow-md p-6">
          <p className="text-sm text-text-secondary mb-1">Receitas do Mês</p>
          <p className="text-xl font-mono text-success">
            {formatCurrency(data.monthlyIncome)}
          </p>
        </div>

        <div className="bg-surface rounded-2xl shadow-md p-6">
          <p className="text-sm text-text-secondary mb-1">Despesas do Mês</p>
          <p className="text-xl font-mono text-expense">
            {formatCurrency(data.monthlyExpense)}
          </p>
        </div>

        <div className="bg-surface rounded-2xl shadow-md p-6">
          <p className="text-sm text-text-secondary mb-1">Saldo do Mês</p>
          <p className="text-xl font-mono text-primary">
            {formatCurrency(data.monthlyBalance)}
          </p>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;