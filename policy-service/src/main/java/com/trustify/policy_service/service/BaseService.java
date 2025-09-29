package com.trustify.policy_service.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic base service interface for CRUD operations
 *
 * @param <T> the entity type
 * @param <ID> the entity ID type
 */
public interface BaseService<T, ID> {
    
    /**
     * Save an entity
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);
    
    /**
     * Find entity by ID
     *
     * @param id the entity ID
     * @return optional entity
     */
    Optional<T> findById(ID id);
    
    /**
     * Get all entities
     *
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Delete entity by ID
     *
     * @param id the entity ID
     */
    void deleteById(ID id);
    
    /**
     * Delete entity
     *
     * @param entity the entity to delete
     */
    void delete(T entity);
    
    /**
     * Check if entity exists by ID
     *
     * @param id the entity ID
     * @return true if entity exists
     */
    boolean existsById(ID id);
}