package com.monexus.finance.category.listener;

import com.monexus.finance.category.repository.CategoryRepository;
import com.monexus.finance.wallet.event.WalletDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CategoryWalletDeletionListener {

    private final CategoryRepository categoryRepository;

    public CategoryWalletDeletionListener(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Order(2)
    @EventListener
    @Transactional
    public void onWalletDeleted(WalletDeletedEvent event) {
        categoryRepository.deleteAll(categoryRepository.findAllByWalletId(event.wallet().getId()));
    }

}
