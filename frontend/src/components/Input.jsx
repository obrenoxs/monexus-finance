function Input({ label, type = "text", value, onChange, error }) {
  return (
    <div className="flex flex-col gap-1 text-left">
      <label className="text-sm text-text-secondary font-sans">
        {label}
      </label>
      <input
        type={type}
        value={value}
        onChange={onChange}
        className="rounded-lg border border-neutral px-4 py-2 text-text-primary
                   focus:outline-none focus:ring-2 focus:ring-primary"
      />
      {error && <span className="text-sm text-expense">{error}</span>}
    </div>
  );
}

export default Input;