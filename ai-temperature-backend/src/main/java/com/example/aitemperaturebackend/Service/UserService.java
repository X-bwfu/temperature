package com.example.aitemperaturebackend.Service;

import com.example.aitemperaturebackend.Model.User;
import com.example.aitemperaturebackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 注册
    public void register(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // 登录
    public boolean login(String username, String password) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) return false;

        User user = optional.get();
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    // 生成重置密码 token（暂时只返回给前端）
    public String createPasswordResetToken(String email) {
        Optional<User> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) {
            throw new RuntimeException("Email not found");
        }

        String token = UUID.randomUUID().toString();
        // 这里你可以把 token 存数据库，但暂时不存（你之后要我会帮你补）

        return token;
    }

    // 重置密码（使用 token）
    public void resetPassword(String token, String newPassword) {
        // ❗ 因为你还没有 PasswordResetToken 表，所以这里只是示例
        throw new RuntimeException("Reset password not implemented (no token storage)");
    }

    // ✔ 已登录用户修改密码
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Old password incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}
