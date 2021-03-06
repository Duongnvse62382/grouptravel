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
 *
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "place_type", schema = "gta_db")
@NamedQueries({
    @NamedQuery(name = "PlaceType.findAll", query = "SELECT p FROM PlaceType p")})
public class PlaceType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "placeType")
    private List<PlaceWithType> placeWithTypeList;

    public PlaceType() {
    }

    public PlaceType(Integer id) {
        this.id = id;
    }

}
