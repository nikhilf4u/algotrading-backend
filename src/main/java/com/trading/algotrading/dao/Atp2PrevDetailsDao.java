package com.trading.algotrading.dao;

import com.trading.algotrading.dto.Atp2PrevDetailsDto;
import com.trading.algotrading.model.Atp2PrevDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface Atp2PrevDetailsDao  extends JpaRepository<Atp2PrevDetails,Integer> {
    @Query(value = "SELECT nse_contract_value  as nseContractValue,nse_total_open_interest  as nseTotalOpenInterest,nse_volume  as nseVolume,atp2_value  as atp2Value FROM atp2_prev_details WHERE expiry_date=?1 AND option_type=?2 AND strike_price=?3",nativeQuery = true)
    Atp2PrevDetailsDto getAtp2Details(String expiryDate,String optionType,Integer strikePrice);

    @Modifying
    @Query(value = "UPDATE atp2_prev_details set nse_contract_value=?1,nse_total_open_interest=?2,nse_volume=?3,atp2_value=?4 WHERE expiry_date=?5 AND option_type=?6 AND strike_price=?7",nativeQuery = true)
    void updateAtp2Details(Double nseContractValue,Double nseTotalOpenInterest,Double nseVolume,Double atp2Value,String expiryDate,String optionType,Integer strikePrice);
}
