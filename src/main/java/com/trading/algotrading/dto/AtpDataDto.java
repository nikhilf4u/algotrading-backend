package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AtpDataDto {
    Integer strikePrice;
    String optionType;
    Double lastPrice;
    Double atp3;
    Double atp1;
    String time;
}
