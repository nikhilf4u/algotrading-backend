package com.trading.algotrading.dto;

public interface StrikePriceSelectionDto {
    Integer getOpenInterest();
    String getStrikePrice();
    Float  getImpliedVolatility();
}
