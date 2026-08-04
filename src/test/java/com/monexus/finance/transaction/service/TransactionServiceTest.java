package com.monexus.finance.transaction.service;

import com.monexus.finance.category.entity.Category;
import com.monexus.finance.category.enums.CategoryType;
import com.monexus.finance.category.service.CategoryService;
import com.monexus.finance.transaction.dto.request.TransactionRequest;
import com.monexus.finance.transaction.dto.response.TransactionResponse;
import com.monexus.finance.transaction.entity.Transaction;
import com.monexus.finance.transaction.enums.TransactionType;
import com.monexus.finance.transaction.exception.TransactionCategoryMismatchException;
import com.monexus.finance.transaction.exception.TransactionNotFoundException;
import com.monexus.finance.transaction.mapper.TransactionMapper;
import com.monexus.finance.transaction.repository.TransactionRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private WalletService walletService;

    @Mock
    private CategoryService categoryService;

    private TransactionService transactionService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        wallet = Wallet.builder().id(10L).currency("BRL").build();
        transactionService = new TransactionService(transactionRepository, transactionMapper, walletService, categoryService);
    }

    @Test
    void shouldCreateTransactionWhenTypeMatchesCategory() {
        TransactionRequest request = new TransactionRequest("Salário", BigDecimal.valueOf(3000), LocalDate.now(), null, TransactionType.INCOME, 5L);
        Category category = Category.builder().id(5L).name("Salário").type(CategoryType.INCOME).build();
        Transaction transactionToSave = Transaction.builder().description("Salário").type(TransactionType.INCOME).build();
        Transaction savedTransaction = Transaction.builder().id(50L).description("Salário").type(TransactionType.INCOME).build();
        TransactionResponse expectedResponse = new TransactionResponse(50L, "Salário", BigDecimal.valueOf(3000), LocalDate.now(), null, TransactionType.INCOME, 5L, "Salário", null);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryService.getCategoryForWallet(wallet, 5L)).thenReturn(category);
        when(transactionMapper.toEntity(request)).thenReturn(transactionToSave);
        when(transactionRepository.save(transactionToSave)).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.createTransaction(user, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(transactionRepository).save(transactionToSave);
    }

    @Test
    void shouldThrowWhenCreatingTransactionWithMismatchedCategoryType() {
        TransactionRequest request = new TransactionRequest("Compra", BigDecimal.valueOf(150), LocalDate.now(), null, TransactionType.EXPENSE, 5L);
        Category category = Category.builder().id(5L).name("Salário").type(CategoryType.INCOME).build();

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryService.getCategoryForWallet(wallet, 5L)).thenReturn(category);

        assertThatThrownBy(() -> transactionService.createTransaction(user, request))
                .isInstanceOf(TransactionCategoryMismatchException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldReturnTransactionWhenOwnedByUser() {
        Transaction transaction = Transaction.builder().id(20L).description("Uber").wallet(wallet).build();
        TransactionResponse expectedResponse = new TransactionResponse(20L, "Uber", BigDecimal.valueOf(25), LocalDate.now(), null, TransactionType.EXPENSE, 5L, "Transporte", null);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(transactionRepository.findByIdAndWalletId(20L, wallet.getId())).thenReturn(Optional.of(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(expectedResponse);

        TransactionResponse response = transactionService.getTransactionById(user, 20L);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldThrowWhenTransactionNotFoundOrNotOwned() {
        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(transactionRepository.findByIdAndWalletId(999L, wallet.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById(user, 999L))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    void shouldUpdateTransactionWhenNewCategoryTypeMatches() {
        Transaction existingTransaction = Transaction.builder().id(20L).description("Uber").type(TransactionType.EXPENSE).wallet(wallet).build();
        Category newCategory = Category.builder().id(6L).name("Transporte").type(CategoryType.EXPENSE).build();
        TransactionRequest request = new TransactionRequest("Uber Eats", BigDecimal.valueOf(40), LocalDate.now(), null, TransactionType.EXPENSE, 6L);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(transactionRepository.findByIdAndWalletId(20L, wallet.getId())).thenReturn(Optional.of(existingTransaction));
        when(categoryService.getCategoryForWallet(wallet, 6L)).thenReturn(newCategory);
        when(transactionRepository.save(existingTransaction)).thenReturn(existingTransaction);
        when(transactionMapper.toResponse(existingTransaction)).thenReturn(
                new TransactionResponse(20L, "Uber Eats", BigDecimal.valueOf(40), LocalDate.now(), null, TransactionType.EXPENSE, 6L, "Transporte", null));

        TransactionResponse response = transactionService.updateTransaction(user, 20L, request);

        assertThat(response.description()).isEqualTo("Uber Eats");
    }

    @Test
    void shouldThrowWhenUpdateChangesToMismatchedCategoryType() {
        Transaction existingTransaction = Transaction.builder().id(20L).description("Uber").type(TransactionType.EXPENSE).wallet(wallet).build();
        Category incomeCategory = Category.builder().id(7L).name("Salário").type(CategoryType.INCOME).build();
        TransactionRequest request = new TransactionRequest("Uber", BigDecimal.valueOf(40), LocalDate.now(), null, TransactionType.EXPENSE, 7L);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(transactionRepository.findByIdAndWalletId(20L, wallet.getId())).thenReturn(Optional.of(existingTransaction));
        when(categoryService.getCategoryForWallet(wallet, 7L)).thenReturn(incomeCategory);

        assertThatThrownBy(() -> transactionService.updateTransaction(user, 20L, request))
                .isInstanceOf(TransactionCategoryMismatchException.class);

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldDeleteTransactionWhenOwnedByUser() {
        Transaction transaction = Transaction.builder().id(20L).description("Uber").wallet(wallet).build();

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(transactionRepository.findByIdAndWalletId(20L, wallet.getId())).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(user, 20L);

        verify(transactionRepository).delete(transaction);
    }
}