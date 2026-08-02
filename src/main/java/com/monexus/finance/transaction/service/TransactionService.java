package com.monexus.finance.transaction.service;

import com.monexus.finance.category.entity.Category;
import com.monexus.finance.category.enums.CategoryType;
import com.monexus.finance.category.service.CategoryService;
import com.monexus.finance.transaction.dto.request.TransactionRequest;
import com.monexus.finance.transaction.dto.response.MonthlySummary;
import com.monexus.finance.transaction.dto.response.TransactionResponse;
import com.monexus.finance.transaction.entity.Transaction;
import com.monexus.finance.transaction.enums.TransactionType;
import com.monexus.finance.transaction.exception.TransactionCategoryMismatchException;
import com.monexus.finance.transaction.exception.TransactionNotFoundException;
import com.monexus.finance.transaction.mapper.TransactionMapper;
import com.monexus.finance.transaction.repository.MonthlyAggregateProjection;
import com.monexus.finance.transaction.repository.TransactionRepository;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletService walletService;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper, WalletService walletService, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.walletService = walletService;
        this.categoryService = categoryService;
    }

    @Transactional
    public TransactionResponse createTransaction(User authenticatedUser, TransactionRequest request) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);
        Category category = categoryService.getCategoryForWallet(wallet, request.categoryId());
        validateTypeConsistency(request.type(), category.getType());

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setWallet(wallet);
        transaction.setCategory(category);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(savedTransaction);
    }

    public List<TransactionResponse> getTransactions(User authenticatedUser) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);
        return transactionRepository.findAllByWalletId(wallet.getId()).stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    public TransactionResponse getTransactionById(User authenticatedUser, Long transactionId) {
        Transaction transaction = findOwnedTransaction(authenticatedUser, transactionId);
        return transactionMapper.toResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(User authenticatedUser, Long transactionId, TransactionRequest request) {
        Transaction transaction = findOwnedTransaction(authenticatedUser, transactionId);
        Category category = categoryService.getCategoryForWallet(transaction.getWallet(), request.categoryId());
        validateTypeConsistency(request.type(), category.getType());

        transaction.setDescription(request.description());
        transaction.setAmount(request.amount());
        transaction.setDate(request.date());
        transaction.setObservation(request.observation());
        transaction.setType(request.type());
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(updatedTransaction);
    }

    @Transactional
    public void deleteTransaction(User authenticatedUser, Long transactionId) {
        Transaction transaction = findOwnedTransaction(authenticatedUser, transactionId);
        transactionRepository.delete(transaction);
    }

    private Transaction findOwnedTransaction(User authenticatedUser, Long transactionId) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);
        return transactionRepository.findByIdAndWalletId(transactionId, wallet.getId())
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }

    private void validateTypeConsistency(TransactionType transactionType, CategoryType categoryType) {
        if (!transactionType.name().equals(categoryType.name())) {
            throw new TransactionCategoryMismatchException(transactionType, categoryType);
        }
    }

    public List<MonthlySummary> getMonthlyHistory(Wallet wallet, LocalDate startDate) {
        List<MonthlyAggregateProjection> aggregates = transactionRepository.findMonthlyAggregates(wallet.getId(), startDate);

        Map<YearMonth, BigDecimal> incomeByMonth = new TreeMap<>();
        Map<YearMonth, BigDecimal> expenseByMonth = new TreeMap<>();

        for (MonthlyAggregateProjection aggregate : aggregates) {
            YearMonth yearMonth = YearMonth.parse(aggregate.getYearMonth());
            if (aggregate.getType() == TransactionType.INCOME) {
                incomeByMonth.put(yearMonth, aggregate.getTotal());
            } else {
                expenseByMonth.put(yearMonth, aggregate.getTotal());
            }
        }

        Set<YearMonth> allMonths = new TreeSet<>();
        allMonths.addAll(incomeByMonth.keySet());
        allMonths.addAll(expenseByMonth.keySet());

        return allMonths.stream()
                .map(month -> {
                    BigDecimal income = incomeByMonth.getOrDefault(month, BigDecimal.ZERO);
                    BigDecimal expense = expenseByMonth.getOrDefault(month, BigDecimal.ZERO);
                    return new MonthlySummary(month, income, expense, income.subtract(expense));
                }).toList();
    }

    public BigDecimal getCurrentBalance(Wallet wallet) {
        return getMonthlyHistory(wallet, null).stream()
                .map(MonthlySummary::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
