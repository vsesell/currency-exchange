package com.serge;

import java.math.BigDecimal;
import java.math.MathContext;

public class Test {
    public static void main(String[] args) {
        var number1 = new BigDecimal("334.23456514919515616541987496");
        var round = number1.round(new MathContext(7));
        System.out.println(number1);
    }
}
