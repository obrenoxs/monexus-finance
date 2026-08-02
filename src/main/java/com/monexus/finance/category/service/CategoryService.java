package com.monexus.finance.category.service;

import com.monexus.finance.category.dto.request.CategoryRequest;
import com.monexus.finance.category.dto.response.CategoryResponse;
import com.monexus.finance.category.entity.Category;
import com.monexus.finance.category.exception.CategoryAlreadyExistsException;
import com.monexus.finance.category.exception.CategoryInUseException;
import com.monexus.finance.category.exception.CategoryNotFoundException;
import com.monexus.finance.category.mapper.CategoryMapper;
import com.monexus.finance.category.repository.CategoryRepository;
import com.monexus.finance.category.validation.CategoryUsageValidator;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final WalletService walletService;
    private final List<CategoryUsageValidator> usageValidators;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, WalletService walletService, List<CategoryUsageValidator> usageValidators) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.walletService = walletService;
        this.usageValidators = usageValidators;
    }

    @Transactional
    public CategoryResponse createCategory(User authenticatedUser, CategoryRequest request) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);

        if (categoryRepository.existsByWalletIdAndNameAndType(wallet.getId(), request.name(), request.type())) {
            throw new CategoryAlreadyExistsException(request.name(), request.type());
        }

        Category category = categoryMapper.toEntity(request);
        category.setWallet(wallet);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    public List<CategoryResponse> getCategories(User authenticatedUser) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);

        return categoryRepository.findAllByWalletId(wallet.getId()).stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(User authenticatedUser, Long categoryId) {
        Category category = findOwnedCategory(authenticatedUser, categoryId);
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(User authenticatedUser, Long categoryId, CategoryRequest request) {
        Category category = findOwnedCategory(authenticatedUser, categoryId);

        boolean nameOrTypeChanged = !category.getName().equals(request.name()) || category.getType() != request.type();

        if (nameOrTypeChanged && categoryRepository.existsByWalletIdAndNameAndType(category.getWallet().getId(), request.name(), request.type())) {
            throw new CategoryAlreadyExistsException(request.name(), request.type());
        }

        category.setName(request.name());
        category.setType(request.type());

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(User authenticatedUser, Long categoryId) {
        Category category = findOwnedCategory(authenticatedUser, categoryId);

        boolean inUse = usageValidators.stream()
                        .anyMatch(validator -> validator.isInUse(category.getId()));

        if (inUse) {
            throw new CategoryInUseException(category.getId());
        }

        categoryRepository.delete(category);
    }

    public Category getCategoryForWallet(Wallet wallet, Long categoryId) {
        return categoryRepository.findByIdAndWalletId(categoryId, wallet.getId())
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private Category findOwnedCategory(User authenticatedUser, Long categoryId) {
        Wallet wallet = walletService.getWalletByUser(authenticatedUser);
        return getCategoryForWallet(wallet, categoryId);
    }
}
