package com.example.aitemperaturebackend.Controller;

import com.example.aitemperaturebackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email) {
        try {
            userService.register(username, password, email);
            return "Registration successful";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        boolean success = userService.login(username, password);
        return success ? "Login successful" : "Login failed";
    }

    @PostMapping("/request-reset-password")
    public String requestReset(@RequestParam String email) {
        try {
            String token = userService.createPasswordResetToken(email);
            return "Password reset token: " + token;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return "Password reset successful";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String username,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        try {
            userService.changePassword(username, oldPassword, newPassword);
            return "Password changed successfully";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


}
