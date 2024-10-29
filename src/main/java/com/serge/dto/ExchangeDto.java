package com.serge.dto;

import com.serge.entity.Currency;

import java.math.BigDecimal;

public class ExchangeDto extends ExchangeRateDto {
    BigDecimal amount;
    BigDecimal convertedAmount;

    public ExchangeDto(Integer id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        super(id, baseCurrency, targetCurrency, rate);
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public ExchangeDto() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
