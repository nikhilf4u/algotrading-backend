package com.trading.algotrading.dao;

import com.trading.algotrading.dto.ContractDto;
import com.trading.algotrading.dto.OverallSummaryDto;
import com.trading.algotrading.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractDao  extends JpaRepository<Contract,Integer> {
    @Query(value = "SELECT price as price,open_interest as openInterest,strike_price as strikePrice,time as time,value as value,atp as atp FROM contract WHERE index_type=?1 and strike_price=?2 and option_type=?3 and timestamp like '2024-03-22%'",nativeQuery = true)
    List<ContractDto> getContracts(String index,String strikePrice,String optionType);

    @Query(value = "SELECT distinct strike_price FROM algotrading.contract where index_type=?1 order by strike_price",nativeQuery = true)
    List<String> getStrikePriceList(String indexType);

    @Query(value = "select sum(value)/10000000 as atpValueSum,time as time,option_type as optionType from contract where timestamp like '2024-03-29%' group by time,option_type",nativeQuery = true)
    List<OverallSummaryDto> getOverallSummary();
}
