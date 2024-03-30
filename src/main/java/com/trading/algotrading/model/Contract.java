package com.trading.algotrading.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(name="price")
    private String price;
    @Column(name="open_interest")
    private String openInterest;
    @Column(name="strike_price")
    private String strikePrice;
    @Column(name="time")
    private String time;
    @Column(name="index_type")
    private String indexType;
    @Column(name="value")
    private String value;
    @Column(name="atp")
    private String atp;
    @Column(name="option_type")
    private String optionType;


}
