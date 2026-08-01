package com.monexus.finance.transaction.listener;

import com.monexus.finance.transaction.repository.TransactionRepository;
import com.monexus.finance.wallet.event.WalletDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WalletDeletionListener {

    private final TransactionRepository transactionRepository;

    public WalletDeletionListener(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Order(1)
    @EventListener
    @Transactional
    public void onWalletDeleted(WalletDeletedEvent event) {
        transactionRepository.deleteAll(transactionRepository.findAllByWalletId(event.wallet().getId()));
    }
}
