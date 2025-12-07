package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Household;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Household} entities.
 * <p>
 * This interface extends {@link CrudRepository} to provide basic CRUD operations for households.
 * It includes custom query methods for finding households by name or name and password.
 * </p>
 */
public interface HouseholdRepository extends CrudRepository<Household, Long> {

    /**
     * Finds a household by its name.
     *
     * @param name the name of the household to search for
     * @return an {@link Optional} containing the {@link Household} if found, or empty otherwise
     */
    Optional<Household> findByName(String name);

    /**
     * Finds a household by its name and password.
     * <p>
     * This method is useful for authentication purposes when verifying household credentials.
     * </p>
     *
     * @param name the name of the household
     * @param password the password of the household
     * @return an {@link Optional} containing the {@link Household} if the name and password match, or empty otherwise
     */
    Optional<Household> findByNameAndPassword(String name, String password);
}
