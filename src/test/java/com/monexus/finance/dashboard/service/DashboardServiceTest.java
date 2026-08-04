package com.monexus.finance.dashboard.service;

import com.monexus.finance.dashboard.dto.response.DashboardResponse;
import com.monexus.finance.dashboard.enums.DashboardPeriod;
import com.monexus.finance.transaction.dto.response.MonthlySummary;
import com.monexus.finance.transaction.service.TransactionService;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionService transactionService;

    private DashboardService dashboardService;

    private User user;
    private Wallet wallet;
    private YearMonth currentYearMonth;
    private LocalDate currentMonthStart;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        wallet = Wallet.builder().id(10L).currency("BRL").build();
        currentYearMonth = YearMonth.now();
        currentMonthStart = currentYearMonth.atDay(1);
        dashboardService = new DashboardService(walletService, transactionService);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
    }

    @Test
    void shouldBuildDashboardWithCurrentMonthIndicatorsAndHistory() {
        MonthlySummary currentMonth = new MonthlySummary(currentYearMonth,
                BigDecimal.valueOf(3000), BigDecimal.valueOf(1200), BigDecimal.valueOf(1800));
        MonthlySummary previousMonth = new MonthlySummary(currentYearMonth.minusMonths(1),
                BigDecimal.valueOf(2800), BigDecimal.valueOf(1100), BigDecimal.valueOf(1700));

        LocalDate last3MonthsStart = DashboardPeriod.LAST_3_MONTHS.resolveStartDate();

        when(transactionService.getMonthlyHistory(wallet, currentMonthStart))
                .thenReturn(List.of(currentMonth));
        when(transactionService.getCurrentBalance(wallet))
                .thenReturn(BigDecimal.valueOf(9500));
        when(transactionService.getMonthlyHistory(wallet, last3MonthsStart))
                .thenReturn(List.of(previousMonth, currentMonth));

        DashboardResponse response = dashboardService.getDashboard(user, DashboardPeriod.LAST_3_MONTHS);

        assertThat(response.currentBalance()).isEqualByComparingTo("9500");
        assertThat(response.monthlyIncome()).isEqualByComparingTo("3000");
        assertThat(response.monthlyExpense()).isEqualByComparingTo("1200");
        assertThat(response.monthlyBalance()).isEqualByComparingTo("1800");
        assertThat(response.history()).containsExactly(previousMonth, currentMonth);
    }

    @Test
    void shouldDefaultToZeroWhenNoTransactionsInCurrentMonth() {
        when(transactionService.getMonthlyHistory(wallet, currentMonthStart))
                .thenReturn(List.of());
        when(transactionService.getCurrentBalance(wallet))
                .thenReturn(BigDecimal.ZERO);

        DashboardResponse response = dashboardService.getDashboard(user, DashboardPeriod.CURRENT_MONTH);

        assertThat(response.monthlyIncome()).isEqualByComparingTo("0");
        assertThat(response.monthlyExpense()).isEqualByComparingTo("0");
        assertThat(response.monthlyBalance()).isEqualByComparingTo("0");
    }

    @Test
    void shouldResolveStartDateFromPeriodAndPassToTransactionService() {
        when(transactionService.getMonthlyHistory(wallet, currentMonthStart))
                .thenReturn(List.of());
        when(transactionService.getCurrentBalance(wallet))
                .thenReturn(BigDecimal.ZERO);
        when(transactionService.getMonthlyHistory(wallet, null))
                .thenReturn(List.of());

        dashboardService.getDashboard(user, DashboardPeriod.ALL_TIME);

        verify(transactionService).getMonthlyHistory(wallet, null);
    }
}
