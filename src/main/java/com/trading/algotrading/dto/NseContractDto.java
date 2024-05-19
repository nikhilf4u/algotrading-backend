package com.trading.algotrading.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NseContractDto {
    String underlying;
    String expiryDate;
    String optionType;
    Integer strikePrice;
    Float lastPrice;
    Long volume;
    Long value;
    Long openInterest;
    Double underlyingValue;
}
