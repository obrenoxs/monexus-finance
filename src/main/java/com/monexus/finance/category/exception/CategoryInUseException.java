package com.monexus.finance.category.exception;

public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException(Long categoryId) {
        super("A categoria " + categoryId + "não pode ser excluída pois possuí movimentações vinculadas.");
    }
}
