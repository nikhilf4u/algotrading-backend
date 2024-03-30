package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractInputDto {
    private Integer id;
    private String price;
    private String openInterest;
    private String time;
    private String strikePrice;
    private String indexType;
    private String value;
    private String atp;
    private String optionType;
}
