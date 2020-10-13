package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "currency", schema = "gta_db")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "symbol")
    private String symbol;
    @OneToMany(mappedBy = "firstCurrency")
    private List<CurrencyRate> firstCurrencyList;
    @OneToMany(mappedBy = "secondCurrency")
    private List<CurrencyRate> secondCurrencyRateList;
}
