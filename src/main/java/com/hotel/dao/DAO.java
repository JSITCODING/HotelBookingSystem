package com.hotel.dao;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    void save(T entity) throws IOException;
    void update(T entity) throws IOException;
    void deleteById(String id) throws IOException;
    Optional<T> findById(String id);
    List<T> findAll();
    void deleteAll() throws IOException;
} 