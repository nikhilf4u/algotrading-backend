package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OptionChainForStrikePriceDto {
    List<Float> priceList;
    List<Integer> openInterestList;
    List<String> timeList;
}
