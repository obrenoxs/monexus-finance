package com.monexus.finance.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Já existe um usuário cadastrado com o email: " + email);
    }
}
