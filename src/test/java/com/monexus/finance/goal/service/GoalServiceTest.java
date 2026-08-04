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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private WalletService walletService;

    private GoalService goalService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        wallet = Wallet.builder().id(10L).currency("BRL").build();
        goalService = new GoalService(goalRepository, goalMapper, walletService);
    }

    @Test
    void shouldCreateGoal() {
        GoalRequest request = new GoalRequest("Viagem", BigDecimal.valueOf(5000), LocalDate.now().plusMonths(6));
        Goal goalToSave = Goal.builder().title("Viagem").targetAmount(BigDecimal.valueOf(5000)).build();
        Goal savedGoal = Goal.builder().id(30L).title("Viagem").targetAmount(BigDecimal.valueOf(5000)).currentAmount(BigDecimal.ZERO).build();
        GoalResponse expectedResponse = new GoalResponse(30L, "Viagem", BigDecimal.valueOf(5000), BigDecimal.ZERO, null, BigDecimal.valueOf(5000), 0.0, null);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(goalMapper.toEntity(request)).thenReturn(goalToSave);
        when(goalRepository.save(goalToSave)).thenReturn(savedGoal);
        when(goalMapper.toResponse(savedGoal)).thenReturn(expectedResponse);

        GoalResponse response = goalService.createGoal(user, request);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnGoalWhenOwnedByUser() {
        Goal goal = Goal.builder().id(30L).title("Viagem").wallet(wallet).build();
        GoalResponse expectedResponse = new GoalResponse(30L, "Viagem", BigDecimal.valueOf(5000), BigDecimal.ZERO, null, BigDecimal.valueOf(5000), 0.0, null);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(goalRepository.findByIdAndWalletId(30L, wallet.getId())).thenReturn(Optional.of(goal));
        when(goalMapper.toResponse(goal)).thenReturn(expectedResponse);

        GoalResponse response = goalService.getGoalById(user, 30L);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldThrowWhenGoalNotFoundOrNotOwned() {
        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(goalRepository.findByIdAndWalletId(999L, wallet.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.getGoalById(user, 999L))
                .isInstanceOf(GoalNotFoundException.class);
    }

    @Test
    void shouldUpdateGoalFieldsWithoutTouchingCurrentAmount() {
        Goal existingGoal = Goal.builder()
                .id(30L).title("Viagem").targetAmount(BigDecimal.valueOf(5000))
                .currentAmount(BigDecimal.valueOf(1000)).wallet(wallet).build();
        GoalRequest request = new GoalRequest("Viagem Europa", BigDecimal.valueOf(8000), LocalDate.now().plusMonths(10));

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(goalRepository.findByIdAndWalletId(30L, wallet.getId())).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(existingGoal)).thenReturn(existingGoal);
        when(goalMapper.toResponse(existingGoal)).thenReturn(
                new GoalResponse(30L, "Viagem Europa", BigDecimal.valueOf(8000), BigDecimal.valueOf(1000), null, BigDecimal.valueOf(7000), 12.5, null));

        goalService.updateGoal(user, 30L, request);

        assertThat(existingGoal.getTitle()).isEqualTo("Viagem Europa");
        assertThat(existingGoal.getTargetAmount()).isEqualByComparingTo("8000");
        assertThat(existingGoal.getCurrentAmount()).isEqualByComparingTo("1000");
    }

    @Test
    void shouldUpdateOnlyCurrentAmountViaProgress() {
        Goal existingGoal = Goal.builder()
                .id(30L).title("Viagem").targetAmount(BigDecimal.valueOf(5000))
                .currentAmount(BigDecimal.valueOf(1000)).wallet(wallet).build();
        GoalProgressRequest request = new GoalProgressRequest(BigDecimal.valueOf(1500));

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(goalRepository.findByIdAndWalletId(30L, wallet.getId())).thenReturn(Optional.of(existingGoal));
        when(goalRepository.save(existingGoal)).thenReturn(existingGoal);
        when(goalMapper.toResponse(existingGoal)).thenReturn(
                new GoalResponse(30L, "Viagem", BigDecimal.valueOf(5000), BigDecimal.valueOf(1500), null, BigDecimal.valueOf(3500), 30.0, null));

        goalService.updateProgress(user, 30L, request);

        assertThat(existingGoal.getCurrentAmount()).isEqualByComparingTo("1500");
        assertThat(existingGoal.getTitle()).isEqualTo("Viagem");
    }

    @Test
    void shouldDeleteGoalWhenOwnedByUser() {
        Goal goal = Goal.builder().id(30L).title("Viagem").wallet(wallet).build();

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(goalRepository.findByIdAndWalletId(30L, wallet.getId())).thenReturn(Optional.of(goal));

        goalService.deleteGoal(user, 30L);

        verify(goalRepository).delete(goal);
    }
}