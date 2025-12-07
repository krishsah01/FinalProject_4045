package com.group5final.roomieradar.repositories;

import com.group5final.roomieradar.entities.Chore;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing {@link Chore} entities.
 * <p>
 * This interface extends {@link CrudRepository} to provide basic CRUD operations for chores.
 * It includes custom query methods for retrieving chores based on household ID.
 * </p>
 */
public interface ChoreRepository extends CrudRepository<Chore, Long> {

    /**
     * Finds all chores associated with a specific household.
     *
     * @param householdId the ID of the household to search for
     * @return an iterable collection of {@link Chore} entities matching the household ID
     */
    Iterable<Chore> findByHouseholdId(Long householdId);
}
