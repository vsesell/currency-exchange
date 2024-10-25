package com.serge.util;

public class ErrorMessages {
    public static final String NO_CURRENCY = "{\n" +
            "    \"message\": \"Валюта не найдена\"\n" +
            "}\n";
    public static final String NO_PARAMETER_CURRENCY = "{\n" +
            "\"message\": \"Нет нужного параметра (name,sign,code)\"\n" +
            "}\n";
    public static final String NO_PARAMETER_EXCHANGE_RATE = "{\n" +
            "\"message\": \"Нет нужного параметра (baseCurrencyCode,targetCurrencyCode,rate)\"\n" +
            "}\n";
    public static final String CURRENCY_ALREADY_EXIST = "{\n" +
            "\"message\": \"Валюта с таким кодом уже существует\"\n" +
            "}\n";
    public static final String EXCHANGE_RATE_ALREADY_EXIST = "{\n" +
            "\"message\": \"Валютная пара уже существует\"\n" +
            "}\n";
    public static final String SIX_CHARACTER = "{\n" +
            "\"message\": \"Длинна запроса должна быть шесть символов (Пример:USDRUB etc.)\"\n" +
            "}\n";
    public static final String NO_EXCHANGE_RATE = "{\n" +
            "\"message\": \"Нет подходящего обменного курса\"\n" +
            "}\n";
    public static final String NO_PARAMETER_RATE = "{\n" +
            "\"message\": \"Нет параметра (rate)\"\n" +
            "}\n";
}
