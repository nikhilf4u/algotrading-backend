package com.trading.algotrading.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="option_chain")
public class OptionChain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(name="price")
    private Float price;
    @Column(name="open_interest")
    private Integer openInterest;
    @Column(name="strike_price")
    private String strikePrice;
    @Column(name="time")
    private String time;
    @Column(name="index_type")
    private String index;
    @Column(name="implied_volatility")
    private Float impliedVolatility;
    @Column(name="delta")
    private Float delta;
    @Column(name="theta")
    private Float theta;
    @Column(name="gamma")
    private Float gamma;
    @Column(name = "vega")
    private Float vega;
    @Column(name="days_left_to_expire")
    private Integer daysLeftToExpire;
}
