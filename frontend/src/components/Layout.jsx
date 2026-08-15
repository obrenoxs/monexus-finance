import { useState, useEffect } from "react";
import { Outlet, Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import ConfirmDialog from "./ConfirmDialog";

function Layout() {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  useEffect(() => {
    setIsMenuOpen(false);
  }, [location.pathname]);

  function confirmLogout() {
    logout();
    navigate("/login");
  }

  const navLinks = [
    { to: "/dashboard", label: "Dashboard" },
    { to: "/categories", label: "Categorias" },
    { to: "/transactions", label: "Transações" },
    { to: "/goals", label: "Metas" },
  ];

  return (
    <div className="min-h-screen bg-background flex overflow-x-hidden">
      {/* Barra superior, visível só no mobile */}
      <header className="md:hidden fixed top-0 left-0 right-0 h-14 bg-surface border-b border-neutral flex items-center justify-between px-4 z-40">
        <span className="text-lg font-sans text-primary">Monexus Finance</span>
        <button
          onClick={() => setIsMenuOpen(true)}
          aria-label="Abrir menu"
          className="flex flex-col gap-1.5 p-2"
        >
          <span className="w-6 h-0.5 bg-text-primary" />
          <span className="w-6 h-0.5 bg-text-primary" />
          <span className="w-6 h-0.5 bg-text-primary" />
        </button>
      </header>

      {/* Fundo escurecido, atrás do menu aberto no mobile */}
      {isMenuOpen && (
        <div
          onClick={() => setIsMenuOpen(false)}
          className="md:hidden fixed inset-0 bg-black/40 z-40"
        />
      )}

      {/* Menu lateral */}
      <aside
        className={`w-60 bg-surface border-r border-neutral p-6 flex flex-col fixed md:static top-0 bottom-0 left-0 z-50
        transition-transform duration-200 ease-in-out
        ${isMenuOpen ? "translate-x-0" : "-translate-x-full"} md:translate-x-0`}
      >
        <h1 className="text-xl font-sans text-primary mb-8 hidden md:block">
          Monexus Finance
        </h1>

        <nav className="flex flex-col gap-2 flex-1 mt-14 md:mt-0">
          {navLinks.map((link) => (
            <Link
              key={link.to}
              to={link.to}
              className="text-text-primary hover:text-primary px-3 py-2 rounded-lg hover:bg-neutral transition"
            >
              {link.label}
            </Link>
          ))}
        </nav>

        <button
          onClick={() => setShowLogoutConfirm(true)}
          className="text-sm text-text-secondary hover:text-expense text-left"
        >
          Sair
        </button>
      </aside>

      <main className="flex-1 pt-14 md:pt-0 w-full min-w-0">
        <Outlet />
      </main>

      <ConfirmDialog
        isOpen={showLogoutConfirm}
        title="Sair da conta"
        message="Tem certeza que deseja sair?"
        onConfirm={confirmLogout}
        onCancel={() => setShowLogoutConfirm(false)}
      />
    </div>
  );
}

export default Layout;