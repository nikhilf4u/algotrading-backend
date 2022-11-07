package com.trading.algotrading.dao;

import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.dto.TotalOIDto;
import com.trading.algotrading.model.OptionChain;
import com.trading.algotrading.model.TotalOI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TotalOIDao extends JpaRepository<TotalOI,Integer> {
    @Query(value = "select total_call_oi as totalCallOI,total_put_oi as totalPutOI,time as time from oi_trend where index_type=?1",nativeQuery = true)
    List<TotalOIDto> getOITrendData(String index);
}
