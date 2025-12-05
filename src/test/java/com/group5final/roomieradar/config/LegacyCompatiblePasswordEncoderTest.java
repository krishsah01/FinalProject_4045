package com.group5final.roomieradar.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LegacyCompatiblePasswordEncoderTest {

    private final LegacyCompatiblePasswordEncoder encoder = new LegacyCompatiblePasswordEncoder();

    @Test
    public void testPlainTextMatch() {
        String raw = "password123";
        String encoded = "password123"; // Simulating DB content
        assertTrue(encoder.matches(raw, encoded), "Plain text password should match");
    }

    @Test
    public void testBCryptMatch() {
        String raw = "password123";
        String encoded = encoder.encode(raw); // BCrypt hash
        assertTrue(encoder.matches(raw, encoded), "BCrypt hashed password should match");
    }

    @Test
    public void testMismatch() {
        String raw = "wrong";
        String encoded = "password123";
        assertFalse(encoder.matches(raw, encoded), "Wrong password should not match");
    }
}
