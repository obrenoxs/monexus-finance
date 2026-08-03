package com.monexus.finance.goal.repository;

import com.monexus.finance.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByWalletId(Long walletId);

    Optional<Goal> findByIdAndWalletId(Long id, Long walletId);
}
