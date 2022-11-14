package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionChainInputDto {
    private Integer id;
    private Float price;
    private Integer openInterest;
    private String time;
    private String strikePrice;
    private String index;
    private Float impliedVolatility;
    private Integer daysLeftToExpire;
    private Integer underlyingValue;
}
