package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StrikePriceContractDataDto {
    List<Double> callLastPriceList;
    List<Double> callMarketPriceValueList;
    List<String> callTimeList;
    List<Double> putLastPriceList;
    List<Double> putMarketPriceValueList;
    List<String> putTimeList;
    List<Double> callATP1List;
    List<Double> putATP1List;
    List<Double> callAtp1ValueList;
    List<Double> putAtp1ValueList;
    List<Double> callATP3List;
    List<Double> putATP3List;
    List<Double> callAtp3ValueList;
    List<Double> putAtp3ValueList;
    Integer selectedStrikePrice;
}
