package com.monexus.finance.category.exception;

import com.monexus.finance.category.enums.CategoryType;

public class CategoryAlreadyExistsException extends RuntimeException{

    public CategoryAlreadyExistsException(String name, CategoryType type) {
        super("Já existe uma categoria de " + type + " com o nome: " + name);
    }
}
