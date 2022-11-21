package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OptionChainForStrikePriceDto {
    List<Float> callPriceList;
    List<Integer> callOpenInterestList;
    List<String> callTimeList;
    List<Float> putPriceList;
    List<Integer> putOpenInterestList;
    List<String> putTimeList;
    List<Integer> callVolumeList;
    List<Integer> putVolumeList;
    List<Integer> overallChangeInOIList;
}
