import { Link } from "react-router-dom";

function Home() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-background px-4 gap-6">
      <h1 className="text-3xl font-sans text-primary text-center">
        Monexus Finance
      </h1>

      <p className="text-text-secondary text-center max-w-sm">
        Organize suas finanças pessoais de forma simples e clara.
      </p>

      <div className="flex gap-4">
        <Link
          to="/login"
          className="bg-primary text-white rounded-lg px-6 py-2 hover:opacity-90 transition"
        >
          Entrar
        </Link>
        <Link
          to="/register"
          className="border border-primary text-primary rounded-lg px-6 py-2 hover:bg-neutral transition"
        >
          Criar conta
        </Link>
      </div>
    </div>
  );
}

export default Home;