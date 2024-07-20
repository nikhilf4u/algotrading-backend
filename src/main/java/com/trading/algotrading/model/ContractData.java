package com.trading.algotrading.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="contract_data")
public class ContractData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(name = "nse_contract_value")
    private Double nseContractValue;
    @Column(name = "nse_total_open_interest")
    private Double nseTotalOpenInterest;
    @Column(name = "nse_volume")
    private Double nseVolume;
    @Column(name = "nse_last_price")
    private Double nseLastPrice;
    @Column(name = "strike_price")
    private Integer strikePrice;
    @Column(name = "option_type")
    private String optionType;
    @Column(name = "market_price_value")
    private Double marketPriceValue;
    @Column(name = "index_type")
    private String indexType;
    @Column(name = "atp1")
    private Double atp1;
    @Column(name = "atp1_value")
    private Double atp1Value;
    @Column(name = "atp2")
    private Double atp2;
    @Column(name = "atp2_value")
    private Double atp2Value;
    @Column(name="atp3")
    private Double atp3;
    @Column(name="atp3_value")
    private Double atp3Value;
    @Column(name = "time")
    private String time;
}
