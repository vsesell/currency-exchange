package com.serge.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<K,T>{
    List<T> findAll();

    Optional<T> findById(Integer id);

    int update(T entity);

    T save(T entity) throws SQLException;
}
