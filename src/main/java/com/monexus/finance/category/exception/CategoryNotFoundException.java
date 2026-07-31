package com.monexus.finance.category.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Categoria não encontrada: " + id);
    }
}
