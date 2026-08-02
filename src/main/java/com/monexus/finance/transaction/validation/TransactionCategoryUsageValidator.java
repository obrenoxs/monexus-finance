package com.monexus.finance.transaction.validation;

import com.monexus.finance.category.validation.CategoryUsageValidator;
import com.monexus.finance.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Component;

@Component
public class TransactionCategoryUsageValidator implements CategoryUsageValidator {

    private final TransactionRepository transactionRepository;

    public TransactionCategoryUsageValidator(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public boolean isInUse(Long categoryId) {
        return transactionRepository.existsByCategoryId(categoryId);
    }
}
