package com.monexus.finance.wallet.listener;

import com.monexus.finance.user.event.UserDeletedEvent;
import com.monexus.finance.wallet.service.WalletService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeletionListener {

    private final WalletService walletService;

    public UserDeletionListener(WalletService walletService) {
        this.walletService = walletService;
    }

    @EventListener
    public void onUserDelete(UserDeletedEvent event) {
        walletService.deleteWalletForUser(event.user());
    }
}
