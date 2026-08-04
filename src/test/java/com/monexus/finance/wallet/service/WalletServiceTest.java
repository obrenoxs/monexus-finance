package com.monexus.finance.wallet.service;

import com.monexus.finance.user.entity.User;
import com.monexus.finance.wallet.entity.Wallet;
import com.monexus.finance.wallet.event.WalletDeletedEvent;
import com.monexus.finance.wallet.exception.WalletNotFoundException;
import com.monexus.finance.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private WalletService walletService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        walletService = new WalletService(walletRepository, eventPublisher);
    }

    @Test
    void shouldCreateWalletWithBrlCurrencyForUser() {
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);

        walletService.createWalletForUser(user);

        verify(walletRepository).save(walletCaptor.capture());
        Wallet savedWallet = walletCaptor.getValue();

        assertThat(savedWallet.getCurrency()).isEqualTo("BRL");
        assertThat(savedWallet.getUser()).isEqualTo(user);
    }

    @Test
    void shouldReturnWalletWhenExists() {
        Wallet wallet = Wallet.builder().id(10L).currency("BRL").user(user).build();

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletByUser(user);

        assertThat(result).isEqualTo(wallet);
    }

    @Test
    void shouldThrowWhenWalletDoesNotExist() {
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> walletService.getWalletByUser(user))
                .isInstanceOf(WalletNotFoundException.class);
    }

    @Test
    void shouldPublishWalletDeletedEventBeforeDeletingWallet() {
        Wallet wallet = Wallet.builder().id(10L).currency("BRL").user(user).build();

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        walletService.deleteWalletForUser(user);

        InOrder inOrder = inOrder(eventPublisher, walletRepository);
        inOrder.verify(eventPublisher).publishEvent(any(WalletDeletedEvent.class));
        inOrder.verify(walletRepository).delete(wallet);
    }
}
