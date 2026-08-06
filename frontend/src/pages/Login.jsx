import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login } from "../services/authService";
import Input from "../components/Input";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    try {
      const data = await login(email, password);
      localStorage.setItem("token", data.token);
      navigate("/dashboard");
    } catch (err) {
      setError("E-mail ou senha inválidos.");
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <form
        onSubmit={handleSubmit}
        className="bg-surface rounded-2xl shadow-md p-8 w-full max-w-sm flex flex-col gap-4"
      >
        <h1 className="text-2xl font-sans text-primary text-center mb-2">
          Monexus Finance
        </h1>

        <Input
          label="E-mail"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <Input
          label="Senha"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {error && <p className="text-sm text-expense text-center">{error}</p>}

        <button
          type="submit"
          className="bg-primary text-white rounded-lg py-2 mt-2 hover:opacity-90 transition"
        >
          Entrar
        </button>

        <p className="text-sm text-text-secondary text-center">
          Não tem uma conta?{" "}
          <Link to="/register" className="text-primary font-medium">
            Cadastre-se
          </Link>
        </p>
      </form>
    </div>
  );
}

export default Login;