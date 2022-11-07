package com.trading.algotrading.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OIChangeDto {
    List<Integer> oiChangeList;
    List<String> timeList;
}
