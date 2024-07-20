package com.trading.algotrading.dto;

public interface ContractDataDto {
    public Double getLastPrice();
    public Double getMarketPriceValue();
    public Double getAtp1();
    public Double getAtp1Value();
    public Double getAtp3();
    public Double getAtp3Value();
    public String getTime();
    public Integer getStrikePrice();
}
