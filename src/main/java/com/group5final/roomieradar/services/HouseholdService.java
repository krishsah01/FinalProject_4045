
package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Household createHousehold(String name, String password, User creator) {
        log.info("Attempting to create household with name: {}", name);
        if (householdRepository.findByName(name).isPresent()) {
            log.warn("Household creation failed: Name {} already exists", name);
            throw new IllegalArgumentException("Household name already exists");
        }
        Household household = new Household();
        household.setName(name);
        household.setPassword(passwordEncoder.encode(password)); // Keep password encoding
        household = householdRepository.save(household);
        log.info("Household created successfully with ID: {} and name: {}", household.getId(), name);

        creator.setHousehold(household);
        userRepository.save(creator);
        log.info("Creator user {} assigned to household {}", creator.getId(), household.getId());

        return household;
    }

    @Transactional
    public void joinHousehold(String name, String password, User user) {
        log.info("User {} attempting to join household with name: {}", user.getId(), name);
        Household household = householdRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("Join failed for user {}: Household with name {} not found", user.getId(), name);
                    return new IllegalArgumentException("Household not found");
                });

        if (!passwordEncoder.matches(password, household.getPassword())) {
             // Fallback for plain text passwords during migration
             if (!password.equals(household.getPassword())) {
                 log.warn("Join failed for user {}: Invalid password for household {}", user.getId(), name);
                 throw new IllegalArgumentException("Invalid household password");
             }
        }

        user.setHousehold(household);
        userRepository.save(user);
        log.info("User {} successfully joined household {}", user.getId(), household.getName());
    }
}

