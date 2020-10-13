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
 *
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "activity_with_document", schema = "gta_db")
public class DocumentWithActivity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_activity", referencedColumnName = "id")
    @ManyToOne
    private Activity activity;
    @JoinColumn(name = "id_document", referencedColumnName = "id")
    @ManyToOne
    private Document document;

    public DocumentWithActivity() {
    }

    public DocumentWithActivity(Integer id) {
        this.id = id;
    }

}
