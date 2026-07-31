package com.monexus.finance.wallet.listener;

import com.monexus.finance.user.event.UserRegisteredEvent;
import com.monexus.finance.wallet.service.WalletService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WalletCreationListener {

    private final WalletService walletService;

    public WalletCreationListener(WalletService walletService) {
        this.walletService = walletService;
    }

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        walletService.createWalletForUser(event.user());
    }
}
