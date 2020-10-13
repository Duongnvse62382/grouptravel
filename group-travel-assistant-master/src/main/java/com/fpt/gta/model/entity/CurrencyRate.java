package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "currency_rate", schema = "gta_db")
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @JoinColumn(name = "id_first_currency", referencedColumnName = "id")
    @ManyToOne
    private Currency firstCurrency;
    @JoinColumn(name = "id_second_currency", referencedColumnName = "id")
    @ManyToOne
    private Currency secondCurrency;
}
