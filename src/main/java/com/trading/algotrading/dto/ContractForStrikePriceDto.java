package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContractForStrikePriceDto {
    List<String> callPriceList;
    List<String> callOpenInterestList;
    List<String> callTimeList;
    List<String> putPriceList;
    List<String> putOpenInterestList;
    List<String> putTimeList;
    List<String> callValueList;
    List<String> putValueList;
    List<String> callAtpList;
    List<String> putAtpList;
    String selectedStrikePrice;
}
