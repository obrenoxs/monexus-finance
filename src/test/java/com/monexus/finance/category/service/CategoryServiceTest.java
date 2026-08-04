package com.monexus.finance.category.service;

import com.monexus.finance.category.dto.request.CategoryRequest;
import com.monexus.finance.category.dto.response.CategoryResponse;
import com.monexus.finance.category.entity.Category;
import com.monexus.finance.category.enums.CategoryType;
import com.monexus.finance.category.exception.CategoryAlreadyExistsException;
import com.monexus.finance.category.exception.CategoryInUseException;
import com.monexus.finance.category.exception.CategoryNotFoundException;
import com.monexus.finance.category.mapper.CategoryMapper;
import com.monexus.finance.category.repository.CategoryRepository;
import com.monexus.finance.category.validation.CategoryUsageValidator;
import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private WalletService walletService;

    @Mock
    private CategoryUsageValidator usageValidator;

    private CategoryService categoryService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository, categoryMapper, walletService, List.of(usageValidator));

        user = User.builder().id(1L).build();
        wallet = Wallet.builder().id(10L).currency("BRL").user(user).build();
    }

    @Test
    void shouldCreateCategoryWhenNoDuplicateExists() {
        CategoryRequest request = new CategoryRequest("Mercado", CategoryType.EXPENSE);
        Category categoryToSave = Category.builder().name("Mercado").type(CategoryType.EXPENSE).build();
        Category savedCategory = Category.builder().id(1L).name("Mercado").type(CategoryType.EXPENSE).wallet(wallet).build();
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Mercado", CategoryType.EXPENSE, LocalDateTime.now());

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.existsByWalletIdAndNameAndType(wallet.getId(), "Mercado", CategoryType.EXPENSE)).thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toResponse(savedCategory)).thenReturn(expectedResponse);

        CategoryResponse response = categoryService.createCategory(user, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(categoryRepository).save(categoryToSave);
    }

    @Test
    void shouldThrowWhenCreatingDuplicateCategory() {
        CategoryRequest request = new CategoryRequest("Mercado", CategoryType.EXPENSE);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.existsByWalletIdAndNameAndType(wallet.getId(), "Mercado", CategoryType.EXPENSE)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(user, request))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldAllowSameNameWithDifferentType() {
        CategoryRequest request = new CategoryRequest("Supermercado", CategoryType.INCOME);
        Category categoryToSave = Category.builder().name("Supermercado").type(CategoryType.INCOME).wallet(wallet).build();
        Category savedCategory = Category.builder().id(101L).name("Supermercado").type(CategoryType.INCOME).build();
        CategoryResponse expectedResponse = new CategoryResponse(101L, "Supermercado", CategoryType.INCOME, null);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.existsByWalletIdAndNameAndType(wallet.getId(), "Supermercado", CategoryType.INCOME))
                .thenReturn(false);
        when(categoryMapper.toEntity(request)).thenReturn(categoryToSave);
        when(categoryRepository.save(categoryToSave)).thenReturn(savedCategory);
        when(categoryMapper.toResponse(savedCategory)).thenReturn(expectedResponse);

        CategoryResponse response = categoryService.createCategory(user, request);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnCategoryWhenOwnedByUserWallet() {
        Category category = Category.builder().id(5L).name("Salário").type(CategoryType.INCOME).wallet(wallet).build();
        CategoryResponse expectedResponse = new CategoryResponse(5L, "Salário", CategoryType.INCOME, LocalDateTime.now());

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.findByIdAndWalletId(5L, wallet.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(expectedResponse);

        CategoryResponse response = categoryService.getCategoryById(user, 5L);

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldThrowWhenCategoryNotFoundOrNotOwned() {
        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.findByIdAndWalletId(99L, wallet.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(user, 99L))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void shouldUpdateCategoryWhenNoConflict() {
        Category existingCategory = Category.builder().id(5L).name("Salário").type(CategoryType.INCOME).wallet(wallet).build();
        CategoryRequest request = new CategoryRequest("Salário CLT", CategoryType.INCOME);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.findByIdAndWalletId(5L, wallet.getId())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByWalletIdAndNameAndType(wallet.getId(), "Salário CLT", CategoryType.INCOME)).thenReturn(false);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toResponse(existingCategory)).thenReturn(
                new CategoryResponse(5L, "Salário CLT", CategoryType.INCOME, LocalDateTime.now())
        );

        CategoryResponse response = categoryService.updateCategory(user, 5L, request);

        assertThat(response.name()).isEqualTo("Salário CLT");
    }

    @Test
    void shouldThrowWhenUpdateCausesConflictWithAnotherCategory() {
        Category existingCategory = Category.builder().id(5L).name("Salário").type(CategoryType.INCOME).wallet(wallet).build();
        CategoryRequest request = new CategoryRequest("Freelance", CategoryType.INCOME);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.findByIdAndWalletId(5L, wallet.getId())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByWalletIdAndNameAndType(wallet.getId(), "Freelance", CategoryType.INCOME)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.updateCategory(user, 5L, request))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldNotThrowWhenUpdateResendsSameValues() {
        Category existingCategory = Category.builder().id(5L).name("Salário").type(CategoryType.INCOME).wallet(wallet).build();
        CategoryRequest request = new CategoryRequest("Salário", CategoryType.INCOME);

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.findByIdAndWalletId(5L, wallet.getId())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);
        when(categoryMapper.toResponse(existingCategory)).thenReturn(
                new CategoryResponse(5L, "Salário", CategoryType.INCOME, LocalDateTime.now())
        );

        categoryService.updateCategory(user, 5L, request);

        verify(categoryRepository, never()).existsByWalletIdAndNameAndType(any(), any(), any());
    }

    @Test
    void shouldDeleteCategoryWhenNotInUse() {
        Category category = Category.builder().id(5L).name("Salário").type(CategoryType.INCOME).wallet(wallet).build();

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.findByIdAndWalletId(5L, wallet.getId())).thenReturn(Optional.of(category));
        when(usageValidator.isInUse(5L)).thenReturn(false);

        categoryService.deleteCategory(user, 5L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldThrowWhenDeletingCategoryInUse() {
        Category category = Category.builder().id(5L).name("Mercado").type(CategoryType.EXPENSE).wallet(wallet).build();

        when(walletService.getWalletByUser(user)).thenReturn(wallet);
        when(categoryRepository.findByIdAndWalletId(5L, wallet.getId())).thenReturn(Optional.of(category));
        when(usageValidator.isInUse(5L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(user, 5L))
                .isInstanceOf(CategoryInUseException.class);

        verify(categoryRepository, never()).delete(any());
    }
}
