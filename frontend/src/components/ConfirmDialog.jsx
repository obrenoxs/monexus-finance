function ConfirmDialog({ isOpen, title, message, onConfirm, onCancel }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 px-4">
      <div className="bg-surface rounded-2xl shadow-md p-6 w-full max-w-sm flex flex-col gap-4">
        <h2 className="text-lg font-sans text-text-primary">{title}</h2>
        <p className="text-sm text-text-secondary">{message}</p>

        <div className="flex justify-end gap-3 mt-2">
          <button
            onClick={onCancel}
            className="px-4 py-2 rounded-lg text-text-secondary hover:bg-neutral transition"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 rounded-lg bg-expense text-white hover:opacity-90 transition"
          >
            Confirmar
          </button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmDialog;