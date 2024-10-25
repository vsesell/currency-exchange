package com.serge.dto;

import com.serge.entity.Currency;

public class ExchangeDto extends ExchangeRateDto {
    Double amount;
    Double convertedAmount;

    public ExchangeDto(Integer id, Currency baseCurrency, Currency targetCurrency, Double rate, Double amount, Double convertedAmount) {
        super(id, baseCurrency, targetCurrency, rate);
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public ExchangeDto() {
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(Double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
