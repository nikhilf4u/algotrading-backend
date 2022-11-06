package com.trading.algotrading.dao;

import com.trading.algotrading.dto.OptionChainDto;
import com.trading.algotrading.model.OptionChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OptionChainDao extends JpaRepository<OptionChain,Integer> {
    @Query(value = "select price as price,open_interest as openInterest,strike_price as strikePrice,time as time from option_chain",nativeQuery = true)
    List<OptionChainDto> getOptionChainData();

    @Query(value = "select price as price,open_interest as openInterest,time as time from option_chain where strike_price=?1",nativeQuery = true)
    List<OptionChainDto> getOptionChainDataForStrikePrice(String strikePrice);
}
