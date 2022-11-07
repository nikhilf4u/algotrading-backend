package com.trading.algotrading.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="oi_trend")
public class TotalOI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(name="total_call_oi")
    private Integer totalCallOI;
    @Column(name="total_put_oi")
    private Integer totalPutOI;
    @Column(name="time")
    private String time;
    @Column(name="index_type")
    private String index;
}
