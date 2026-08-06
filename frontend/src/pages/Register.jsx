import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register } from "../services/authService";
import Input from "../components/Input";

function Register() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    try {
      await register(firstName, lastName, email, password);
      setSuccess(true);
      setTimeout(() => navigate("/login"), 2000);
    } catch (err) {
      setError("Não foi possível concluir o cadastro. Verifique os dados informados.");
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
          label="Nome"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
        />

        <Input
          label="Sobrenome"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
        />

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
        {success && (
          <p className="text-sm text-success text-center">
            Cadastro realizado! Verifique seu e-mail para confirmar a conta.
          </p>
        )}

        <button
          type="submit"
          className="bg-primary text-white rounded-lg py-2 mt-2 hover:opacity-90 transition"
        >
          Criar conta
        </button>

        <p className="text-sm text-text-secondary text-center">
          Já tem uma conta?{" "}
          <Link to="/login" className="text-primary font-medium">
            Entrar
          </Link>
        </p>
      </form>
    </div>
  );
}

export default Register;