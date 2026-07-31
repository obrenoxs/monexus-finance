package com.monexus.finance.category.repository;

import com.monexus.finance.category.entity.Category;
import com.monexus.finance.category.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByWalletId(Long walletId);

    Optional<Category> findByIdAndWalletId(Long id, Long walletId);

    boolean existsByWalletIdAndNameAndType(Long walletId, String name, CategoryType type);
}
