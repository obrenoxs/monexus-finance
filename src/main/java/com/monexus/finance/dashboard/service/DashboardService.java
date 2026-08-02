package com.monexus.finance.dashboard.service;

import com.monexus.finance.dashboard.dto.response.DashboardResponse;
import com.monexus.finance.dashboard.enums.DashboardPeriod;
import com.monexus.finance.transaction.dto.response.MonthlySummary;
import com.monexus.finance.transaction.service.TransactionService;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.service.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
public class DashboardService {

    private final WalletService walletService;
    private final TransactionService transactionService;

    public DashboardService(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    public DashboardResponse getDashboard(User authenticatedUser, DashboardPeriod period) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);

        YearMonth currentYearMonth = YearMonth.now();
        List<MonthlySummary> currentMonthData = transactionService.getMonthlyHistory(wallet, currentYearMonth.atDay(1));
        MonthlySummary currentMonth = currentMonthData.stream()
                .filter(summary -> summary.yearMonth().equals(currentYearMonth))
                .findFirst()
                .orElse(new MonthlySummary(currentYearMonth, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

        BigDecimal currentBalance = transactionService.getCurrentBalance(wallet);
        List<MonthlySummary> history = transactionService.getMonthlyHistory(wallet, period.resolveStartDate());

        return new DashboardResponse(
                currentBalance,
                currentMonth.income(),
                currentMonth.expense(),
                currentMonth.balance(),
                history
        );
    }
}
