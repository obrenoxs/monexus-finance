package com.monexus.finance.wallet.dto.response;

import java.time.LocalDateTime;

public record WalletResponse(
        Long id,
        String currency,
        LocalDateTime createdAt
) {}
