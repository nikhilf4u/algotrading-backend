package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ContractSummaryDto {
    List<String> putAtpSumList;
    List<String> timeList;
    List<String> callAtpSumList;
}
