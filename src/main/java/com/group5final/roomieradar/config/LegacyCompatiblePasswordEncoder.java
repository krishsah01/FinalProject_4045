package com.group5final.roomieradar.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A PasswordEncoder that delegates to BCryptPasswordEncoder for new passwords,
 * but falls back to plain text comparison for legacy passwords.
 * This allows existing users to login without resetting their passwords.
 */
public class LegacyCompatiblePasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            System.out.println("LegacyCompatiblePasswordEncoder: Encoded password is null");
            return false;
        }

        // Check if it looks like a BCrypt hash
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
            boolean matches = bcrypt.matches(rawPassword, encodedPassword);
            System.out.println("LegacyCompatiblePasswordEncoder: BCrypt match result: " + matches);
            return matches;
        }

        // Fallback to plain text check
        boolean matches = encodedPassword.equals(rawPassword.toString());
        System.out.println("LegacyCompatiblePasswordEncoder: Plain text match result: " + matches);
        return matches;
    }
}
