package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StrikePriceOutputDto {
    private String strikePrice;
    private Float delta;
    private Float theta;
    private Float gamma;
    private Float vega;
    private Integer volume;
}
