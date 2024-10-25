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
import java.util.Optional;

public class ExchangeService {
    CurrencyDao currencyDao = CurrencyDao.getInstance();
    ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private static final ExchangeService INSTANCE = new ExchangeService();
    private static final Integer USD_CODE = 2;


    public String doExchange(String base, String target, Double amount) {
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
                    amount * rate.get().getRate()
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
            Double invert = 1 / invertedRate.get().getRate();
            ExchangeDto dto = new ExchangeDto(
                    invertedRate.get().getId(),
                    currencyDao.findById(invertedRate.get().getBaseCurrency()).get(),
                    currencyDao.findById(invertedRate.get().getTargetCurrency()).get(),
                    invert,
                    amount,
                    amount * invert
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
            var crossRate = targetRate / baseRate;
            ExchangeDto dto = new ExchangeDto(
                    99,
                    currencyDao.findById(exchangeRateToBase.get().getTargetCurrency()).get(),
                    currencyDao.findById(exchangeRateToTarget.get().getTargetCurrency()).get(),
                    crossRate,
                    amount,
                    crossRate * amount
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
