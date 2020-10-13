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

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "transaction_detail", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "TransactionDetail.findAll", query = "SELECT t FROM TransactionDetail t")})
public class TransactionDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "id_type")
    private Integer idType;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;
    @JoinColumn(name = "id_member", referencedColumnName = "id")
    @ManyToOne
    private Member member;
    @JoinColumn(name = "id_transaction", referencedColumnName = "id")
    @ManyToOne
    private Transaction transaction;

    public TransactionDetail() {
    }

    public TransactionDetail(Integer id) {
        this.id = id;
    }

}
