package com.trading.algotrading.service;

import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.dto.TotalOITrendDto;

import java.util.List;

public interface TotalOIService {
    TotalOITrendDto getTotalOITrendData(String index);
    Boolean saveTotalOIData(Integer totalCallOI,Integer totalPutOI,String time,String index);

}
