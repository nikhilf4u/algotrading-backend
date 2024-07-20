package com.trading.algotrading.dao;

import com.trading.algotrading.dto.Atp2PrevDetailsDto;
import com.trading.algotrading.dto.Atp3InitialDetailsDto;
import com.trading.algotrading.model.Atp2PrevDetails;
import com.trading.algotrading.model.Atp3InitialDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface Atp3InitialDetailDao extends JpaRepository<Atp3InitialDetails,Integer> {
    @Query(value = "SELECT atp3_value as atp3Value,reset as reset,total_open_interest as totalOpenInterest FROM atp3_initial_details WHERE expiry_date=?1 AND option_type=?2 AND strike_price=?3",nativeQuery = true)
    Atp3InitialDetailsDto getAtp3Details(String expiryDate, String optionType, Integer strikePrice);

    @Modifying
    @Query(value = "UPDATE atp3_initial_details set atp3_value=?1,total_open_interest=?2,reset=1 WHERE expiry_date=?3 AND option_type=?4 AND strike_price=?5",nativeQuery = true)
    void updateAtp3AndTotalOpenInterestValue(Double lastAtp3Value,Double lastTotalOpenInterest,String expiryDate,String optionType,Integer strikePrice);
}
