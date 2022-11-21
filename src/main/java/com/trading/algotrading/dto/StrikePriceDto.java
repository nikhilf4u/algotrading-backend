package com.trading.algotrading.dto;

public interface StrikePriceDto {
    String getStrikePrice();
    Float getDelta();
    Float getTheta();
    Float getGamma();
    Float getVega();
    Integer getVolume();
}
