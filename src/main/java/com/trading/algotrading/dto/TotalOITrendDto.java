package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class TotalOITrendDto {
    List<Integer> totalCallOIList;
    List<Integer> totalPutOIList;
    List<String> timeList;
}
