package com.serge.dao;

import com.serge.entity.Currency;
import com.serge.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Integer, Currency> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String FIND_ALL = "SELECT * FROM currencies";
    private static final String FIND_BY_CODE = "SELECT * FROM currencies where code = ?";
    private static final String FIND_BY_ID = "SELECT * FROM currencies where id = ?";
    private static final String SAVE = "INSERT INTO currencies values (?,?,?,?)";
    @Override
    public List<Currency> findAll() {
        try(var connection = ConnectionManager.get();
            var preparedStatement = connection.prepareStatement(FIND_ALL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildEntity(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> findByCode(String code){
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_CODE)){
            preparedStatement.setObject(1,code);
            var resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()){
                currency = buildEntity(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Currency> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setObject(1,id);
            var resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()){
                currency = buildEntity(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(Currency entity) {
        return 0;
    }

    @Override
    public Currency save(Currency entity) throws SQLException {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, null);
            preparedStatement.setObject(2, entity.getCode());
            preparedStatement.setObject(3, entity.getFullName());
            preparedStatement.setObject(4, entity.getSign());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            entity.setId(generatedKeys.getInt(1));
            return entity;
        }

    }
    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private Currency buildEntity(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getObject("id",Integer.class),
                resultSet.getObject("code",String.class),
                resultSet.getObject("full_name",String.class),
                resultSet.getObject("sign",String.class)
        );
    }
}
