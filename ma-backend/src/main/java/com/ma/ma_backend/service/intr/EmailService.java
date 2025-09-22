package com.ma.ma_backend.service.intr;

public interface EmailService {
    void sendActivationEmail(String to, String username, String activationToken);
}