package com.trustify.policy_service.service.impl;

import com.trustify.policy_service.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Base implementation of BaseService interface
 *
 * @param <T> the entity type
 * @param <ID> the entity ID type
 * @param <R> the repository type
 */
public abstract class BaseServiceImpl<T, ID, R extends JpaRepository<T, ID>> implements BaseService<T, ID> {
    
    protected final R repository;
    
    public BaseServiceImpl(R repository) {
        this.repository = repository;
    }
    
    @Override
    public T save(T entity) {
        return repository.save(entity);
    }
    
    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }
    
    @Override
    public List<T> findAll() {
        return repository.findAll();
    }
    
    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
    
    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }
    
    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }
}