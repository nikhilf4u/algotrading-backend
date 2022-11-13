package com.trading.algotrading.dao;

import com.trading.algotrading.dto.OIChangeResponseDto;
import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.dto.StrikePriceSelectionDto;
import com.trading.algotrading.model.OptionChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OptionChainDao extends JpaRepository<OptionChain,Integer> {
    @Query(value = "select price as price,open_interest as openInterest,strike_price as strikePrice,time as time from option_chain",nativeQuery = true)
    List<OptionChainDto> getOptionChainData();

    @Query(value = "select price as price,open_interest as openInterest,time as time from option_chain where strike_price=?1",nativeQuery = true)
    List<OptionChainDto> getOptionChainDataForStrikePrice(String strikePrice);

    @Query(value="select sum(open_interest) as totalOpenInterest,time as time from option_chain where index_type=?1 and strike_price like '%CE' group by time",nativeQuery = true)
    List<OIChangeResponseDto> getTotalCallOI(String index);
    @Query(value="select sum(open_interest) as totalOpenInterest,time as time from option_chain where index_type=?1 and strike_price like '%PE' group by time",nativeQuery = true)
    List<OIChangeResponseDto> getTotalPutOI(String index);

    @Query(value = "SELECT strike_price as strikePrice,implied_volatility as impliedVolatility,open_interest as openInterest FROM option_chain where index_type=?1  order by timestamp desc limit 42",nativeQuery = true)
    List<StrikePriceSelectionDto> getOptionChainDataForStrikePriceSelection(String indexType);
}