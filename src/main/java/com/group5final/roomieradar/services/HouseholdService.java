package com.group5final.roomieradar.services;

import com.group5final.roomieradar.entities.Household;
import com.group5final.roomieradar.entities.User;
import com.group5final.roomieradar.repositories.HouseholdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing household-related operations.
 * <p>
 * This service handles creating and joining households, including validation.
 * </p>
 */
@Service
public class HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    /**
     * Creates a new household with the given name and password, assigning the creator.
     * <p>
     * Validates uniqueness of name and saves the household.
     * </p>
     *
     * @param name the name of the household
     * @param password the password
     * @param creator the user creating the household
     * @throws IllegalArgumentException if name is already taken
     */
    public void createHousehold(String name, String password, User creator) {
        if (householdRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Household name is already taken");
        }

        Household household = new Household();
        household.setName(name);
        household.setPassword(password);
        householdRepository.save(household);

        creator.setHousehold(household);
    }

    /**
     * Joins a user to an existing household using name and password.
     * <p>
     * Validates credentials and updates the user's household.
     * </p>
     *
     * @param name the name of the household
     * @param password the password
     * @param user the user joining
     * @throws IllegalArgumentException if household not found or invalid password
     */
    public void joinHousehold(String name, String password, User user) {
        Household household = householdRepository.findByNameAndPassword(name, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid household name or password"));

        user.setHousehold(household);
    }
}
