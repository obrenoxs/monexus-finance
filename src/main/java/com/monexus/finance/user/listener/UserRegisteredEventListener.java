package com.monexus.finance.user.listener;

import com.monexus.finance.user.event.UserRegisteredEvent;
import com.monexus.finance.user.service.EmailConfirmationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegisteredEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);

    private final EmailConfirmationService emailConfirmationService;

    public UserRegisteredEventListener(EmailConfirmationService emailConfirmationService) {
        this.emailConfirmationService = emailConfirmationService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        try {
            emailConfirmationService.sendConfirmationEmail(event.user());
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de confirmação para {}: {}", event.user().getEmail(), e.getMessage());
        }
    }
}
