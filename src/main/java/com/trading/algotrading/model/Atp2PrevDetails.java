package com.trading.algotrading.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="atp2_prev_details")
public class Atp2PrevDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(name = "expiry_date")
    private String expiryDate;
    @Column(name = "nse_contract_value")
    private Double nseContractValue;
    @Column(name = "nse_total_open_interest")
    private Double nseTotalOpenInterest;
    @Column(name = "nse_volume")
    private Double nseVolume;
    @Column(name = "atp2_value")
    private Double atp2Value;
    @Column(name = "option_type")
    private String optionType;
    @Column(name = "strike_price")
    private Integer strikePrice;
}
