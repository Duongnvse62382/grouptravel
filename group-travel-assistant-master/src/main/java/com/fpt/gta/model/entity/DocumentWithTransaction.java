
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "transaction_with_document", schema = "gta_db")
public class DocumentWithTransaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_document", referencedColumnName = "id")
    @ManyToOne
    private Document document;
    @JoinColumn(name = "id_transaction", referencedColumnName = "id")
    @ManyToOne
    private Transaction transaction;

    public DocumentWithTransaction() {
    }
}
