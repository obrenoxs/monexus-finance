package com.monexus.finance.goal.service;

import com.monexus.finance.goal.dto.request.GoalProgressRequest;
import com.monexus.finance.goal.dto.request.GoalRequest;
import com.monexus.finance.goal.dto.response.GoalResponse;
import com.monexus.finance.goal.entity.Goal;
import com.monexus.finance.goal.exception.GoalNotFoundException;
import com.monexus.finance.goal.mapper.GoalMapper;
import com.monexus.finance.goal.repository.GoalRepository;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final WalletService walletService;

    public GoalService(GoalRepository goalRepository, GoalMapper goalMapper, WalletService walletService) {
        this.goalRepository = goalRepository;
        this.goalMapper = goalMapper;
        this.walletService = walletService;
    }

    @Transactional
    public GoalResponse createdGoal(User authenticatedUser, GoalRequest request) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);

        Goal goal = goalMapper.toEntity(request);
        goal.setWallet(wallet);

        Goal savedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(savedGoal);
    }

    public List<GoalResponse> getGoals(User authenticatedUser) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);
        return goalRepository.findAllByWalletId(wallet.getId()).stream()
                .map(goalMapper::toResponse)
                .toList();
    }

    public GoalResponse getGoalById(User authenticatedUser, Long goalId) {
        Goal goal = findOwnedGoal(authenticatedUser, goalId);
        return goalMapper.toResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoal(User authenticatedUser, Long goalId, GoalRequest request) {
        Goal goal = findOwnedGoal(authenticatedUser, goalId);

        goal.setTitle(request.title());
        goal.setTargetAmount(request.targetAmount());
        goal.setTargetDate(request.targetDate());

        Goal updateGoal = goalRepository.save(goal);
        return goalMapper.toResponse(updateGoal);
    }

    @Transactional
    public GoalResponse updateProgress(User authenticatedUser, Long goalId, GoalProgressRequest request) {
        Goal goal = findOwnedGoal(authenticatedUser, goalId);
        goal.setCurrentAmount(request.currentAmount());

        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toResponse(updatedGoal);
    }

    @Transactional
    public void deleteGoal(User authenticatedUser, Long goalId) {
        Goal goal = findOwnedGoal(authenticatedUser, goalId);
        goalRepository.delete(goal);
    }

    private Goal findOwnedGoal(User authenticatedUser, Long goalId) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);
        return goalRepository.findByIdAndWalletId(goalId, wallet.getId())
                .orElseThrow(() -> new GoalNotFoundException(goalId));
    }
}
