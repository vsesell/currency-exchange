package com.serge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serge.dao.CurrencyDao;
import com.serge.dao.ExchangeRateDao;
import com.serge.dto.ExchangeRateDto;
import com.serge.entity.ExchangeRate;
import com.serge.util.ErrorMessages;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public String findAllExchangeRates() {
        var all = exchangeRateDao.findAll();
        List<ExchangeRateDto> dtos = new ArrayList<>();
        for (ExchangeRate exchangeRate : all) {
            ExchangeRateDto rateDto = new ExchangeRateDto(
                    exchangeRate.getId(),
                    currencyDao.findById(exchangeRate.getBaseCurrency()).get(),
                    currencyDao.findById(exchangeRate.getTargetCurrency()).get(),
                    exchangeRate.getRate()
            );
            dtos.add(rateDto);

        }

        var mapper = new ObjectMapper();
        try(StringWriter sw = new StringWriter();) {
            mapper.writeValue(sw,dtos);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String findRateByCodes(String code1, String code2) {
        var firstId = currencyDao.findByCode(code1).get().getId();
        var secondId = currencyDao.findByCode(code2).get().getId();

        var byBaseAndTargetCurrencies = exchangeRateDao.findByBaseAndTargetCurrencies(
                firstId,
                secondId
        );
        if (byBaseAndTargetCurrencies.isEmpty()) {
            throw new RuntimeException(ErrorMessages.NO_EXCHANGE_RATE);
        }
        ExchangeRateDto dto = new ExchangeRateDto(
                byBaseAndTargetCurrencies.get().getId(),
                currencyDao.findById(byBaseAndTargetCurrencies.get().getBaseCurrency()).get(),
                currencyDao.findById(byBaseAndTargetCurrencies.get().getTargetCurrency()).get(),
                byBaseAndTargetCurrencies.get().getRate()
        );
        var mapper = new ObjectMapper();
        try (var sw = new StringWriter();) {
            mapper.writeValue(sw,dto);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String saveExchangeRate(String baseERCode,String targetBRCode, Double rate){
        try (var sw = new StringWriter()){
            var entity = exchangeRateDao.save(new ExchangeRate(
                    null,
                    currencyDao.findByCode(baseERCode).get().getId(),
                    currencyDao.findByCode(targetBRCode).get().getId(),
                    rate
            ));
            var dto = new ExchangeRateDto(
                    entity.getId(),
                    currencyDao.findById(entity.getBaseCurrency()).get(),
                    currencyDao.findById(entity.getTargetCurrency()).get(),
                    entity.getRate()
            );
            var mapper = new ObjectMapper();
            mapper.writeValue(sw,dto);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String updateExchangeRate(String baseExchangeRate, String targetExchangeRate, Double rate) {
        var firstId = currencyDao.findByCode(baseExchangeRate).get().getId();
        var secondId = currencyDao.findByCode(targetExchangeRate).get().getId();

        var byBaseAndTargetCurrencies = exchangeRateDao.findByBaseAndTargetCurrencies(
                firstId,
                secondId
        );
        if (byBaseAndTargetCurrencies.isEmpty()) {
            throw new RuntimeException(ErrorMessages.NO_EXCHANGE_RATE);
        }
        byBaseAndTargetCurrencies.get().setRate(rate);
        var i = exchangeRateDao.update(byBaseAndTargetCurrencies.get());
        if (i != 0) {
            var updated = exchangeRateDao.findByBaseAndTargetCurrencies(
                    firstId,
                    secondId
            );
            var dto = new ExchangeRateDto(
                    updated.get().getId(),
                    currencyDao.findById(updated.get().getBaseCurrency()).get(),
                    currencyDao.findById(updated.get().getTargetCurrency()).get(),
                    updated.get().getRate()
            );
            var mapper = new ObjectMapper();
            try (StringWriter sw = new StringWriter()) {
                mapper.writeValue(sw,dto);
                return sw.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "Something went wrong hahahah";
    }
    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
