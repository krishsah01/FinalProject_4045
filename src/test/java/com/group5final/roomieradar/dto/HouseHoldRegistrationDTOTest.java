// java
package com.group5final.roomieradar.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HouseholdRegistrationDTOTest {

    private static ValidatorFactory vf;
    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        vf = Validation.buildDefaultValidatorFactory();
        validator = vf.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        vf.close();
    }

    @Test
    void validDto_hasNoViolations_andGettersSettersWork() {
        HouseholdRegistrationDTO dto = new HouseholdRegistrationDTO();
        dto.setName("MyHouse");
        dto.setPassword("secret1");

        Set<ConstraintViolation<HouseholdRegistrationDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no validation violations for valid DTO");

        // getters
        assertEquals("MyHouse", dto.getName());
        assertEquals("secret1", dto.getPassword());
    }

    @Test
    void nameBlank_triggersNotBlankOrSizeViolation() {
        HouseholdRegistrationDTO dto = new HouseholdRegistrationDTO();
        dto.setName("");
        dto.setPassword("secret1");

        Set<ConstraintViolation<HouseholdRegistrationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        // collect messages
        boolean hasNotBlank = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Household name is required")
                        || v.getMessage().contains("Household name must be between"));
        assertTrue(hasNotBlank, "Expected household name constraint violation message");
    }

    @Test
    void nameTooShort_triggersSizeViolation() {
        HouseholdRegistrationDTO dto = new HouseholdRegistrationDTO();
        dto.setName("ab"); // < 3
        dto.setPassword("secret1");

        Set<ConstraintViolation<HouseholdRegistrationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Household name must be between 3 and 50 characters")));
    }

    @Test
    void nameTooLong_triggersSizeViolation() {
        HouseholdRegistrationDTO dto = new HouseholdRegistrationDTO();
        dto.setName("a".repeat(51)); // > 50
        dto.setPassword("secret1");

        Set<ConstraintViolation<HouseholdRegistrationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Household name must be between 3 and 50 characters")));
    }

    @Test
    void passwordBlank_triggersNotBlankViolation() {
        HouseholdRegistrationDTO dto = new HouseholdRegistrationDTO();
        dto.setName("HouseA");
        dto.setPassword("  ");

        Set<ConstraintViolation<HouseholdRegistrationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void passwordTooShort_triggersSizeViolation() {
        HouseholdRegistrationDTO dto = new HouseholdRegistrationDTO();
        dto.setName("HouseA");
        dto.setPassword("12345"); // 5 < 6

        Set<ConstraintViolation<HouseholdRegistrationDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password must be at least 6 characters")));
    }

    @Test
    void lombokEqualsAndHashCode_andToString() {
        HouseholdRegistrationDTO a = new HouseholdRegistrationDTO();
        a.setName("HouseX");
        a.setPassword("passwd");

        HouseholdRegistrationDTO b = new HouseholdRegistrationDTO();
        b.setName("HouseX");
        b.setPassword("passwd");

        HouseholdRegistrationDTO c = new HouseholdRegistrationDTO();
        c.setName("Other");
        c.setPassword("passwd");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        String ts = a.toString();
        assertTrue(ts.contains("HouseX") || ts.contains("passwd"), "toString should include field data");
    }
}