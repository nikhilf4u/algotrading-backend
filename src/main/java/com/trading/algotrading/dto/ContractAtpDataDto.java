package com.trading.algotrading.dto;

public interface ContractAtpDataDto {
    public Integer getStrikePrice();
    public String getOptionType();
    public Double getLastPrice();
    public Double getAtp1();
    public Double getAtp3();
    public String getTime();
}
