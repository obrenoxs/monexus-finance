package com.monexus.finance.goal.mapper;

import com.monexus.finance.goal.dto.response.GoalResponse;
import com.monexus.finance.goal.entity.Goal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GoalMapperTest {

    private final GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    @Test
    void shouldCalculateRemainingAmount() {
        Goal goal = Goal.builder()
                .id(1L).title("Viagem")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(250))
                .build();

        GoalResponse response = goalMapper.toResponse(goal);

        assertThat(response.remainingAmount()).isEqualByComparingTo("750");
    }

    @Test
    void shouldCalculateProgressPercentage() {
        Goal goal = Goal.builder()
                .id(1L).title("Viagem")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(250))
                .build();

        GoalResponse response = goalMapper.toResponse(goal);

        assertThat(response.progressPercentage()).isCloseTo(25.0, within(0.01));
    }

    @Test
    void shouldReachExactlyOneHundredPercentWhenCurrentEqualsTarget() {
        Goal goal = Goal.builder()
                .id(1L).title("Viagem")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(1000))
                .build();

        GoalResponse response = goalMapper.toResponse(goal);

        assertThat(response.progressPercentage()).isCloseTo(100.0, within(0.01));
        assertThat(response.remainingAmount()).isEqualByComparingTo("0");
    }

    @Test
    void shouldAllowProgressAboveOneHundredPercentWhenCurrentExceedsTarget() {
        Goal goal = Goal.builder()
                .id(1L).title("Viagem")
                .targetAmount(BigDecimal.valueOf(1000))
                .currentAmount(BigDecimal.valueOf(1200))
                .build();

        GoalResponse response = goalMapper.toResponse(goal);

        assertThat(response.progressPercentage()).isCloseTo(120.0, within(0.01));
        assertThat(response.remainingAmount()).isEqualByComparingTo("-200");
    }

    @Test
    void shouldReturnZeroProgressWhenTargetAmountIsZero() {
        Goal goal = Goal.builder()
                .id(1L).title("Meta inválida")
                .targetAmount(BigDecimal.ZERO)
                .currentAmount(BigDecimal.valueOf(100))
                .build();

        GoalResponse response = goalMapper.toResponse(goal);

        assertThat(response.progressPercentage()).isCloseTo(0.0, within(0.01));
    }
}