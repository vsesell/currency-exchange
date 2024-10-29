package com.serge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serge.dao.CurrencyDao;
import com.serge.dao.ExchangeRateDao;
import com.serge.dto.ExchangeDto;
import com.serge.dto.ExchangeRateDto;
import com.serge.entity.ExchangeRate;
import com.serge.util.ErrorMessages;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {
    CurrencyDao currencyDao = CurrencyDao.getInstance();
    ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private static final ExchangeService INSTANCE = new ExchangeService();
    private static final Integer USD_CODE = 2;


    public String doExchange(String base, String target, BigDecimal amount) {
        var baseId = currencyDao.findByCode(base).get().getId();
        var targetId = currencyDao.findByCode(target).get().getId();
        var rate = exchangeRateDao.findByBaseAndTargetCurrencies(baseId, targetId);
        if (rate.isPresent()) {
            ExchangeDto dto = new ExchangeDto(
                    rate.get().getId(),
                    currencyDao.findById(rate.get().getBaseCurrency()).get(),
                    currencyDao.findById(rate.get().getTargetCurrency()).get(),
                    rate.get().getRate(),
                    amount,
                    amount.multiply(rate.get().getRate())
            );

            var mapper = new ObjectMapper();
            try (var sw = new StringWriter()) {
                mapper.writeValue(sw, dto);
                return sw.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (exchangeRateDao.findByBaseAndTargetCurrencies(targetId, baseId).isPresent()) {
            var invertedRate = exchangeRateDao.findByBaseAndTargetCurrencies(targetId, baseId);
            BigDecimal invert = new BigDecimal("1").divide(invertedRate.get().getRate(), 2, RoundingMode.HALF_UP);
            ExchangeDto dto = new ExchangeDto(
                    invertedRate.get().getId(),
                    currencyDao.findById(invertedRate.get().getBaseCurrency()).get(),
                    currencyDao.findById(invertedRate.get().getTargetCurrency()).get(),
                    invert,
                    amount,
                    amount.multiply(invert)
            );
            var mapper = new ObjectMapper();
            try (var sw = new StringWriter()) {
                mapper.writeValue(sw, dto);
                return sw.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            throw new RuntimeException(ErrorMessages.NO_EXCHANGE_RATE);
        } else {
            var exchangeRateToBase = exchangeRateDao.findByBaseAndTargetCurrencies(USD_CODE, baseId);
            var exchangeRateToTarget = exchangeRateDao.findByBaseAndTargetCurrencies(USD_CODE, targetId);
            var baseRate = exchangeRateToBase.get().getRate();
            var targetRate = exchangeRateToTarget.get().getRate();
            var crossRate = targetRate.divide(baseRate, 2, RoundingMode.HALF_UP);
            ExchangeDto dto = new ExchangeDto(
                    99,
                    currencyDao.findById(exchangeRateToBase.get().getTargetCurrency()).get(),
                    currencyDao.findById(exchangeRateToTarget.get().getTargetCurrency()).get(),
                    crossRate,
                    amount,
                    crossRate.multiply(amount)
            );
            var mapper = new ObjectMapper();
            try (var sw = new StringWriter()) {
                mapper.writeValue(sw, dto);
                return sw.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static ExchangeService getINSTANCE() {
        return INSTANCE;
    }
}
