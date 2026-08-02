package com.monexus.finance.transaction.exception;

import com.monexus.finance.category.enums.CategoryType;
import com.monexus.finance.transaction.enums.TransactionType;

public class TransactionCategoryMismatchException extends RuntimeException {

    public TransactionCategoryMismatchException(TransactionType transactionType, CategoryType categoryType) {
        super("O tipo da transação (" + transactionType + ") não corresponde ao tipo categoria informada (" + categoryType + ").");
    }
}
