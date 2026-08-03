package com.monexus.finance.goal.exception;

public class GoalNotFoundException extends RuntimeException {

    public GoalNotFoundException(Long id) {
        super("Meta não encontrada: " + id);
    }
}
