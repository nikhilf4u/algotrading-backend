package com.trading.algotrading.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="atp3_initial_details")
public class Atp3InitialDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(name = "expiry_date")
    private String expiryDate;
    @Column(name = "atp3_value")
    private Double atp3Value;
    @Column(name = "option_type")
    private String optionType;
    @Column(name = "strike_price")
    private Integer strikePrice;
    @Column(name = "reset")
    private Integer reset;
    @Column(name = "total_open_interest")
    private Double totalOpenInterest;
}
