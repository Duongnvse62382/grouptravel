/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "document", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "Document.findAll", query = "SELECT d FROM Document d")})
public class Document implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "uri")
    private String uri;
    @Column(name = "thumbnail_uri")
    private String thumbnailUri;
    @Column(name = "content_type")
    private String contentType;
    @Transient
    private String downloadThumbnailUrl;
    @Transient
    private String downloadUrl;


    @OneToMany(mappedBy = "document", cascade = CascadeType.REMOVE)
    private List<DocumentWithActivity> documentWithActivityList;

    @OneToMany(mappedBy = "document", cascade = CascadeType.REMOVE)
    private List<DocumentWithTransaction> documentWithTransactionList;

    @JoinColumn(name = "id_journey", referencedColumnName = "id")
    @ManyToOne
    private Group group;

    public Document() {
    }


}
