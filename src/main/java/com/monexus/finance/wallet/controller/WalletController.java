package com.monexus.finance.wallet.controller;

import com.monexus.finance.user.security.CustomUserDetails;
import com.monexus.finance.wallet.dto.response.WalletResponse;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.mapper.WalletMapper;
import com.monexus.finance.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletMapper walletMapper;

    public WalletController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Wallet wallet = walletService.getWalletByUser(userDetails.getUser());
        WalletResponse response = walletMapper.toResponse(wallet);
        return ResponseEntity.ok(response);
    }
}
