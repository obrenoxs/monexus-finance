package com.monexus.finance.transaction.controller;

import com.monexus.finance.transaction.dto.request.TransactionRequest;
import com.monexus.finance.transaction.dto.response.TransactionResponse;
import com.monexus.finance.transaction.service.TransactionService;
import com.monexus.finance.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(userDetails.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TransactionResponse> response = transactionService.getTransactions(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        TransactionResponse response = transactionService.getTransactionById(userDetails.getUser(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.updateTransaction(userDetails.getUser(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        transactionService.deleteTransaction(userDetails.getUser(), id);
        return ResponseEntity.noContent().build();
    }
}
