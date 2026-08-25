package com.example.bai1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/assistant")
public class PaymentAssistantController {

    private static final Logger log = LoggerFactory.getLogger(PaymentAssistantController.class);
    private final ChatClient chatClient;

    public PaymentAssistantController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/chat")
    public Map<String, Object> chat(@RequestParam(defaultValue = "Kiểm tra số dư tài khoản thanh toán") String message) {
        log.info("Processing RikkeiPay Assistant request: {}", message);

        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();

        return Map.of(
                "status", "SUCCESS",
                "query", message,
                "response", response
        );
    }
}