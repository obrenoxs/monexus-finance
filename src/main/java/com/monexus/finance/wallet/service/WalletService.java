package com.monexus.finance.wallet.service;

import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.exception.WalletNotFoundException;
import com.monexus.finance.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private static final String DEFAULT_CURRENCY = "BRL";

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void createWalletForUser(User user) {
        Wallet wallet = Wallet.builder()
                .currency(DEFAULT_CURRENCY)
                .user(user)
                .build();

        walletRepository.save(wallet);
    }

    public Wallet getWalletByUser(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new WalletNotFoundException(user.getId()));
    }
}
