package com.queuesystem.controller;

import com.queuesystem.service.EmailService;
import com.queuesystem.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// login, register, forgot password and reset password pages
@Controller
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // ---------- login ----------

    // the actual login check is done by Spring Security, we only show the page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ---------- register ----------

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {

        if (fullName.isBlank() || email.isBlank()) {
            model.addAttribute("error", "Please fill in all the fields");
            return "register";
        }
        if (password.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters");
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        try {
            userService.register(fullName, email, password);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        return "redirect:/login?registered";
    }

    // ---------- forgot password ----------

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        String token = userService.createResetToken(email);

        if (token != null) {
            String link = baseUrl + "/reset-password?token=" + token;

            // also print it in the console so you can test without Gmail working
            System.out.println("Password reset link for " + email + ": " + link);

            try {
                emailService.sendResetEmail(email.trim().toLowerCase(), link);
            } catch (Exception e) {
                System.out.println("Could not send email: " + e.getMessage());
            }
        }

        // same message whether the email exists or not, so nobody can
        // use this page to check which emails have accounts
        model.addAttribute("message", "If that email has an account, we sent a reset link to it. Check your inbox.");
        return "forgot-password";
    }

    // ---------- reset password ----------

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        if (!userService.isTokenValid(token)) {
            model.addAttribute("invalid", true);
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String password,
                                @RequestParam String confirmPassword,
                                Model model) {

        if (password.length() < 8) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Password must be at least 8 characters");
            return "reset-password";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("error", "Passwords do not match");
            return "reset-password";
        }

        boolean worked = userService.resetPassword(token, password);
        if (!worked) {
            model.addAttribute("invalid", true);
            return "reset-password";
        }

        return "redirect:/login?reset";
    }
}
