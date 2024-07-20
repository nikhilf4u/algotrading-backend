package com.trading.algotrading.dao;

import com.trading.algotrading.dto.*;
import com.trading.algotrading.model.Contract;
import com.trading.algotrading.model.ContractData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractDataDao extends JpaRepository<ContractData,Integer> {
    @Query(value = "SELECT nse_last_price as lastPrice,market_price_value as marketPriceValue,atp1 as atp1,atp1_value as atp1Value,atp3 as atp3,atp3_value as atp3Value,time as time,strike_price as strikePrice FROM contract_data WHERE index_type=?1 and strike_price=?2 and option_type=?3 and timestamp like ?4",nativeQuery = true)
    List<ContractDataDto> getContractData(String index, Integer strikePrice, String optionType, String currentDate);

    @Query(value = "SELECT distinct strike_price FROM contract_data where index_type=?1 order by strike_price",nativeQuery = true)
    List<Integer> getStrikePriceList(String indexType);

    @Query(value = "select sum(value)/10000000 as atpValueSum,time as time,option_type as optionType from contract where timestamp like '2024-04-08%' group by time,option_type",nativeQuery = true)
    List<OverallSummaryDto> getOverallContractSummary();

    @Query(value = "SELECT atp3_value as atp3Value,nse_total_open_interest as totalOpenInterest FROM contract_data WHERE strike_price=?1 AND option_type=?2 and timestamp like '2024-06-28%' order by timestamp desc limit 1;",nativeQuery = true)
    Atp3AndTotalOIDto getLastAtp3AndTotalOpenInterestValue(Integer strikePrice, String optionType);

    @Query(value = "Select id as id,nse_total_open_interest as openInterest,strike_price as strikePrice,option_type as optionType,atp1 as atp1,time as time from contract_data where timestamp like '2024-05-31%' order by time,strike_price",nativeQuery = true)
    List<DataCorrectionDto> getContractDataForCorrection();

    @Query(value = "update contract_data set atp3=?1,atp3_value=?2 where id=?3",nativeQuery = true)
    @Modifying
    void correctAtp3Values(Double atp3,Double atp3Value,Integer id);

    @Query(value = "Select strike_price as strikePrice,option_type as optionType,nse_last_price as lastPrice,atp1 as atp1,atp3 as atp3,time as time from contract_data where strike_price between ?1 and ?2 order by timestamp desc,strike_price limit 21",nativeQuery = true)
    List<ContractAtpDataDto> getAtpData(Integer startStrikePrice,Integer endStrikePrice);
}
