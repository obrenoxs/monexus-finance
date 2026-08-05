package com.monexus.finance.transaction.repository;

import com.monexus.finance.category.entity.Category;
import com.monexus.finance.category.enums.CategoryType;
import com.monexus.finance.category.repository.CategoryRepository;
import com.monexus.finance.config.TestcontainersConfig;
import com.monexus.finance.transaction.entity.Transaction;
import com.monexus.finance.transaction.enums.TransactionType;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.user.repository.UserRepository;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
class TransactionRepositoryIT {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private Wallet wallet;
    private Category incomeCategory;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
                .firstName("Breno").lastName("Teste")
                .email("integration-" + System.nanoTime() + "@example.com")
                .password("hashed").emailVerified(true).build());

        wallet = walletRepository.save(Wallet.builder().currency("BRL").user(user).build());

        incomeCategory = categoryRepository.save(Category.builder()
                .name("Salário").type(CategoryType.INCOME).wallet(wallet).build());

        expenseCategory = categoryRepository.save(Category.builder()
                .name("Mercado").type(CategoryType.EXPENSE).wallet(wallet).build());
    }

    @Test
    void shouldAggregateTransactionsByMonthAndType() {
        transactionRepository.save(buildTransaction("Salário Junho", BigDecimal.valueOf(3000),
                LocalDate.of(2026, 6, 5), TransactionType.INCOME, incomeCategory));
        transactionRepository.save(buildTransaction("Mercado Junho", BigDecimal.valueOf(500),
                LocalDate.of(2026, 6, 10), TransactionType.EXPENSE, expenseCategory));
        transactionRepository.save(buildTransaction("Salário Julho", BigDecimal.valueOf(3200),
                LocalDate.of(2026, 7, 5), TransactionType.INCOME, incomeCategory));

        List<MonthlyAggregateProjection> result = transactionRepository.findMonthlyAggregates(wallet.getId(), null);

        assertThat(result).hasSize(3);
        assertThat(result).anySatisfy(row -> {
            assertThat(row.getYearMonth()).isEqualTo("2026-06");
            assertThat(row.getType()).isEqualTo(TransactionType.INCOME);
            assertThat(row.getTotal()).isEqualByComparingTo("3000");
        });
        assertThat(result).anySatisfy(row -> {
            assertThat(row.getYearMonth()).isEqualTo("2026-06");
            assertThat(row.getType()).isEqualTo(TransactionType.EXPENSE);
            assertThat(row.getTotal()).isEqualByComparingTo("500");
        });
        assertThat(result).anySatisfy(row -> {
            assertThat(row.getYearMonth()).isEqualTo("2026-07");
            assertThat(row.getType()).isEqualTo(TransactionType.INCOME);
            assertThat(row.getTotal()).isEqualByComparingTo("3200");
        });
    }

    @Test
    void shouldFilterByStartDateWhenProvided() {
        transactionRepository.save(buildTransaction("Antiga", BigDecimal.valueOf(1000),
                LocalDate.of(2025, 1, 10), TransactionType.INCOME, incomeCategory));
        transactionRepository.save(buildTransaction("Recente", BigDecimal.valueOf(2000),
                LocalDate.of(2026, 6, 10), TransactionType.INCOME, incomeCategory));

        List<MonthlyAggregateProjection> result = transactionRepository.findMonthlyAggregates(
                wallet.getId(), LocalDate.of(2026, 1, 1));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getYearMonth()).isEqualTo("2026-06");
    }

    @Test
    void shouldSumMultipleTransactionsInSameMonthAndType() {
        transactionRepository.save(buildTransaction("Mercado 1", BigDecimal.valueOf(200),
                LocalDate.of(2026, 6, 3), TransactionType.EXPENSE, expenseCategory));
        transactionRepository.save(buildTransaction("Mercado 2", BigDecimal.valueOf(300),
                LocalDate.of(2026, 6, 20), TransactionType.EXPENSE, expenseCategory));

        List<MonthlyAggregateProjection> result = transactionRepository.findMonthlyAggregates(wallet.getId(), null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTotal()).isEqualByComparingTo("500");
    }

    private Transaction buildTransaction(String description, BigDecimal amount, LocalDate date,
                                         TransactionType type, Category category) {
        return Transaction.builder()
                .description(description).amount(amount).date(date)
                .type(type).wallet(wallet).category(category)
                .build();
    }
}
