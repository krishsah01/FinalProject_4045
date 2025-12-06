package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import com.group5final.roomieradar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Household createHousehold(String name, String password, User creator) {
        if (householdRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Household name already exists");
        }
        Household household = new Household();
        household.setName(name);
        household.setPassword(passwordEncoder.encode(password));
        household = householdRepository.save(household);
        
        creator.setHousehold(household);
        userRepository.save(creator);
        
        return household;
    }

    @Transactional
    public void joinHousehold(String name, String password, User user) {
        Household household = householdRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Household not found"));

        if (!passwordEncoder.matches(password, household.getPassword())) {
             // Fallback for plain text passwords during migration
             if (!password.equals(household.getPassword())) {
                 throw new IllegalArgumentException("Invalid household password");
             }
        }

        user.setHousehold(household);
        userRepository.save(user);
    }
}
