function CurrencyInput({ label, value, onChange }) {
  function handleChange(event) {
    const digitsOnly = event.target.value.replace(/\D/g, "");
    const cents = digitsOnly === "" ? 0 : parseInt(digitsOnly, 10);
    onChange(cents / 100);
  }

  const displayValue = new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value || 0);

  return (
    <div className="flex flex-col gap-1 text-left">
      <label className="text-sm text-text-secondary font-sans">{label}</label>
      <input
        type="text"
        inputMode="numeric"
        value={displayValue}
        onChange={handleChange}
        className="rounded-lg border border-neutral px-4 py-2 text-text-primary font-mono
                   focus:outline-none focus:ring-2 focus:ring-primary"
      />
    </div>
  );
}

export default CurrencyInput;