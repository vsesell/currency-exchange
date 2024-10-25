package com.serge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serge.dao.CurrencyDao;
import com.serge.entity.Currency;
import com.serge.util.ErrorMessages;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();


    public static CurrencyService getInstance() {
            return INSTANCE;
    }

    public String findAllCurrencies(){
        var all = currencyDao.findAll();

        ObjectMapper mapper = new ObjectMapper();

        try(StringWriter sw = new StringWriter();) {
            mapper.writeValue(sw,all);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public String findCurrencyByCode(String code) {
        var result = currencyDao.findByCode(code);
        if (result.isEmpty()){
            throw new RuntimeException(ErrorMessages.NO_CURRENCY);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try(StringWriter sw = new StringWriter();) {
                mapper.writeValue(sw,result.get());
                return sw.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String saveCurrency(String code,String name,String sign) {

        try (StringWriter sw = new StringWriter();) {
            var entity = currencyDao.save(
                    new Currency(null, code, name, sign)
            );
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(sw, entity);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            return ErrorMessages.NO_PARAMETER_CURRENCY;
        }
    }
}
