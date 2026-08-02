package com.monexus.finance.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(Long id) {
        super("Transação não encontrada: " + id);
    }
}
