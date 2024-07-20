package com.trading.algotrading.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NseContractDataDto {
    String underlying;
    String expiryDate;
    String optionType;
    Integer strikePrice;
    Double lastPrice;
    Double volume;
    Double value;
    Double openInterest;
    Double underlyingValue;
}
