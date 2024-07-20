package com.trading.algotrading.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NseContractDataMapper {
    List<NseContractDataDto> data;
    String timestamp;
}
