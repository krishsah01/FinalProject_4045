package com.group5final.roomieradar.services;

import com.group5final.roomieradar.dto.UserRegistrationDTO;
import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Attempting to register user: {}", registrationDTO.getUsername());
        if (userRepository.findByUsername(registrationDTO.getUsername()).isPresent()) {
            log.warn("Registration failed: Username {} already exists", registrationDTO.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        Household household = null;
        if ("join".equals(registrationDTO.getHouseholdAction())) {
            household = householdRepository.findByName(registrationDTO.getHouseholdName())
                    .orElseThrow(() -> new IllegalArgumentException("Household not found"));
            
            if (!passwordEncoder.matches(registrationDTO.getHouseholdPassword(), household.getPassword())) {
                // Fallback for plain text passwords during migration
                if (!registrationDTO.getHouseholdPassword().equals(household.getPassword())) {
                     throw new IllegalArgumentException("Invalid household password");
                }
            }
        } else if ("create".equals(registrationDTO.getHouseholdAction())) {
            if (householdRepository.findByName(registrationDTO.getNewHouseholdName()).isPresent()) {
                throw new IllegalArgumentException("Household name already exists");
            }
            household = new Household();
            household.setName(registrationDTO.getNewHouseholdName());
            household.setPassword(passwordEncoder.encode(registrationDTO.getNewHouseholdPassword()));
            household = householdRepository.save(household);
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        
        if (household != null) {
            user.setHousehold(household);
        }

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
    }
}
