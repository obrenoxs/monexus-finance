package com.monexus.finance.goal.mapper;

import com.monexus.finance.goal.dto.request.GoalRequest;
import com.monexus.finance.goal.dto.response.GoalResponse;
import com.monexus.finance.goal.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentAmount", ignore = true)
    @Mapping(target = "wallet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Goal toEntity(GoalRequest request);

    @Mapping(target = "remainingAmount", expression = "java(goal.getTargetAmount().subtract(goal.getCurrentAmount()))")
    @Mapping(target = "progressPercentage", expression = "java(calculateProgress(goal))")
    GoalResponse toResponse(Goal goal);

    default double calculateProgress(Goal goal) {
        if (goal.getTargetAmount() == null || goal.getTargetAmount().signum() == 0) {
            return 0.0;
        }
        return goal.getCurrentAmount()
                .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(100))
                .doubleValue();
    }
}
