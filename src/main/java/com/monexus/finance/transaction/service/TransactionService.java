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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
