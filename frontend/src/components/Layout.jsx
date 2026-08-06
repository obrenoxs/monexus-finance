import { Outlet, Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Layout() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="min-h-screen bg-background flex">
      <aside className="w-60 bg-surface border-r border-neutral p-6 flex flex-col">
        <h1 className="text-xl font-sans text-primary mb-8">
          Monexus Finance
        </h1>

        <nav className="flex flex-col gap-2 flex-1">
          <Link
            to="/dashboard"
            className="text-text-primary hover:text-primary px-3 py-2 rounded-lg hover:bg-neutral transition"
          >
            Dashboard
          </Link>
          <Link
            to="/categories"
            className="text-text-primary hover:text-primary px-3 py-2 rounded-lg hover:bg-neutral transition"
          >
            Categorias
          </Link>
          <Link
            to="/transactions"
            className="text-text-primary hover:text-primary px-3 py-2 rounded-lg hover:bg-neutral transition"
          >
            Transações
          </Link>
          <Link
            to="/goals"
            className="text-text-primary hover:text-primary px-3 py-2 rounded-lg hover:bg-neutral transition"
          >
            Metas
          </Link>
        </nav>

        <button
          onClick={handleLogout}
          className="text-sm text-text-secondary hover:text-expense text-left"
        >
          Sair
        </button>
      </aside>

      <main className="flex-1">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;