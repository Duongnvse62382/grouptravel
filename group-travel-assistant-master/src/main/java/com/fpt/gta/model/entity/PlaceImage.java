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
@Table(name = "place_image", schema = "gta_db")
@NamedQueries({
    @NamedQuery(name = "PlaceImage.findAll", query = "SELECT p FROM PlaceImage p")})
public class PlaceImage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "uri")
    private String uri;
    @Column(name = "height")
    private Integer height;
    @Column(name = "width")
    private Integer width;
    @JoinColumn(name = "id_place", referencedColumnName = "id")
    @ManyToOne
    private Place place;

    public PlaceImage() {
    }

    public PlaceImage(Integer id) {
        this.id = id;
    }

}
