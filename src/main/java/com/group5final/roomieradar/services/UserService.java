package com.group5final.roomieradar.services;

import com.group5final.roomieradar.dto.UserRegistrationDTO;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing user-related operations.
 * <p>
 * This service handles user registration, including validation and password encoding.
 * </p>
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user based on the provided registration DTO.
     * <p>
     * Validates uniqueness of username, encodes the password, and saves the user.
     * Throws IllegalArgumentException on validation failures.
     * </p>
     *
     * @param registrationDTO the user registration data
     * @throws IllegalArgumentException if username is taken or other validation fails
     */
    public void registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        // Set other fields from DTO as needed (e.g., email, household)

        userRepository.save(user);
    }
}
