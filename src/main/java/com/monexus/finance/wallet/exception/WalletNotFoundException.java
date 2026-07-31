package com.monexus.finance.wallet.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(Long userId) {
        super("Nenhuma carteira encontrada para o usuário: " + userId);
    }
}
