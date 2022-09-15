package com.daysoft.vending_machine;
import java.util.LinkedHashMap;
import java.math.BigDecimal;

public class Common {
  protected static LinkedHashMap<String,BigDecimal> coinTypes = new LinkedHashMap<String,BigDecimal>(){{
    put("£2", BigDecimal.valueOf(2.00));
    put("£1", BigDecimal.valueOf(1.00));
    put("50p", BigDecimal.valueOf(0.50));
    put("20p", BigDecimal.valueOf(0.20));
    put("10p", BigDecimal.valueOf(0.10));
    put("5p", BigDecimal.valueOf(0.05));
    put("2p", BigDecimal.valueOf(0.02));
    put("1p", BigDecimal.valueOf(0.01));
  }};
}
