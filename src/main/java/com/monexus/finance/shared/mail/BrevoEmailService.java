package com.monexus.finance.shared.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {

    private final RestClient restClient;
    private final String senderEmail;
    private final String senderName;

    public BrevoEmailService(@Value("${brevo.api-key}") String apiKey,
                             @Value("${app.mail.from}") String senderEmail,
                             @Value("${app.mail.sender-name:Monexus Finance}") String senderName) {
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public void send(String to, String subject, String textContent) {
        Map<String, Object> payload = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "textContent", textContent
        );

        restClient.post()
                .uri("/smtp/email")
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}