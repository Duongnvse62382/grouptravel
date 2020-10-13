/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "transaction", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t")})
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "id_type")
    private Integer idType;
    @Column(name = "occur_at")
    private Timestamp occurAt;
    @Column(name = "id_category")
    private Integer idCategory;

    @Column(name = "custom_currency_rate")
    private BigDecimal customCurrencyRate;
    @Transient
    private BigDecimal defaultCurrencyRate;


    @JoinColumn(name = "id_owner", referencedColumnName = "id")
    @ManyToOne
    private Member owner;
    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<TransactionDetail> transactionDetailList;

    @Transient
    private List<Document> documentList;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.REMOVE)
    private List<DocumentWithTransaction> documentWithTransactionList;

    @JoinColumn(name = "id_currency", referencedColumnName = "id")
    @ManyToOne
    private Currency currency;

//    @JoinColumn(name = "id_occur_city", referencedColumnName = "id")
//    @ManyToOne
//    private Trip occurTrip;

    @Transient
    private List<Member> memberList;

    public Transaction() {
    }

    public Transaction(Integer id) {
        this.id = id;
    }


}
