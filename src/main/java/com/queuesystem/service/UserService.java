package com.queuesystem.service;

import com.queuesystem.model.User;
import com.queuesystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // create account
    public void register(String fullName, String email, String password) {
        email = email.trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("That email is already registered");
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    // makes a reset token that lasts 30 minutes
    // returns null if there is no account with that email
    public String createResetToken(String email) {
        Optional<User> found = userRepository.findByEmail(email.trim().toLowerCase());
        if (found.isEmpty()) {
            return null;
        }

        User user = found.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        return token;
    }

    // checks the token exists and has not expired
    public boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Optional<User> found = userRepository.findByResetToken(token);
        if (found.isEmpty()) {
            return false;
        }
        return found.get().getResetTokenExpiry().isAfter(LocalDateTime.now());
    }

    // sets the new password, returns false if the token is no good
    public boolean resetPassword(String token, String newPassword) {
        if (!isTokenValid(token)) {
            return false;
        }

        User user = userRepository.findByResetToken(token).get();
        user.setPassword(passwordEncoder.encode(newPassword));
        // token can only be used once
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }
}
