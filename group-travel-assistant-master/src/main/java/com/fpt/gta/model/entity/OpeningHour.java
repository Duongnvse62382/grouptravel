/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

/**
 *
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "opening_hour", schema = "gta_db")
@NamedQueries({
    @NamedQuery(name = "OpeningHour.findAll", query = "SELECT o FROM OpeningHour o")})
public class OpeningHour implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "day_in_week")
    private Integer dayInWeek;
    @Column(name = "open_at")
    private LocalTime openAt;
    @Column(name = "close_at")
    private LocalTime closeAt;
    @JoinColumn(name = "id_place", referencedColumnName = "id")
    @ManyToOne
    private Place place;

    public OpeningHour() {
    }

    public OpeningHour(Integer id) {
        this.id = id;
    }

}
