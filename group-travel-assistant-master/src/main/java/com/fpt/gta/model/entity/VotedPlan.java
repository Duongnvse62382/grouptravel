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
@Table(name = "voted_plan", schema = "gta_db")
@NamedQueries({
    @NamedQuery(name = "VotedPlan.findAll", query = "SELECT v FROM VotedPlan v")})
public class VotedPlan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_member", referencedColumnName = "id")
    @ManyToOne
    private Member member;
    @JoinColumn(name = "id_plan", referencedColumnName = "id")
    @ManyToOne
    private Plan plan;

    public VotedPlan() {
    }

    public VotedPlan(Integer id) {
        this.id = id;
    }

}
