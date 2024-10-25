package com.serge.dao;

import com.serge.entity.ExchangeRate;
import com.serge.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<Integer, ExchangeRate>{
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private static final String FIND_ALL = "SELECT * FROM exchange_rates";
    private static final String FIND_BY_ID = "SELECT * FROM exchange_rates where id = ?";
    private static final String SAVE = "INSERT INTO exchange_rates values (?,?,?,?)";
    private static final String UPDATE = "UPDATE exchange_rates SET rate = ? where base_currency_id = ? AND target_currency_id = ?";
    public static final String FIND_RATE_BY_BASE_AND_TARGET_CURRENCY = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL)) {
            var resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildEntity(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setObject(1,id);
            var resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()){
                exchangeRate = buildEntity(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRate> findByBaseAndTargetCurrencies(Integer baseCurrency, Integer targetCurrency) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_RATE_BY_BASE_AND_TARGET_CURRENCY)){
            preparedStatement.setObject(1,baseCurrency);
            preparedStatement.setObject(2,targetCurrency);
            var resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildEntity(resultSet);
            }
            return Optional.ofNullable(exchangeRate);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int update(ExchangeRate entity) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setObject(1,entity.getRate());
            preparedStatement.setObject(2,entity.getBaseCurrency());
            preparedStatement.setObject(3,entity.getTargetCurrency());

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1,null);
            preparedStatement.setObject(2,entity.getBaseCurrency());
            preparedStatement.setObject(3,entity.getTargetCurrency());
            preparedStatement.setObject(4,entity.getRate());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            entity.setId(generatedKeys.getInt(1));
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRate buildEntity(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getObject("id", Integer.class),
                resultSet.getObject("base_currency_id", Integer.class),
                resultSet.getObject("target_currency_id", Integer.class),
                resultSet.getObject("rate", Double.class)
        );
    }
    public static ExchangeRateDao getInstance(){
        return INSTANCE;
    }

    public Integer updateExchangeRateByCurrenciesIds(Integer id1, Integer id2, Double newRate) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE)) {
            preparedStatement.setObject(1,newRate);
            preparedStatement.setObject(2,id1);
            preparedStatement.setObject(3,id2);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
