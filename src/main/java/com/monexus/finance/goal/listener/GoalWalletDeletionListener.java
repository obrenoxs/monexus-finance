package com.monexus.finance.goal.listener;

import com.monexus.finance.goal.repository.GoalRepository;
import com.monexus.finance.wallet.event.WalletDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GoalWalletDeletionListener {

    private final GoalRepository goalRepository;

    public GoalWalletDeletionListener(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Order(3)
    @EventListener
    @Transactional
    public void onWalletDeleted(WalletDeletedEvent event) {
        goalRepository.deleteAll(goalRepository.findAllByWalletId(event.wallet().getId()));
    }
}
