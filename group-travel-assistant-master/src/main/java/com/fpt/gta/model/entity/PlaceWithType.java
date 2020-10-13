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
@Table(name = "place_with_type", schema = "gta_db")
@NamedQueries({
    @NamedQuery(name = "PlaceWithType.findAll", query = "SELECT p FROM PlaceWithType p")})
public class PlaceWithType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_place", referencedColumnName = "id")
    @ManyToOne
    private Place place;
    @JoinColumn(name = "id_place_type", referencedColumnName = "id")
    @ManyToOne
    private PlaceType placeType;

    public PlaceWithType() {
    }

    public PlaceWithType(Integer id) {
        this.id = id;
    }

}
