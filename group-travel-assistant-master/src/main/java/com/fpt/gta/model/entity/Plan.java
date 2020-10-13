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
import java.util.List;

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "plan", schema = "gta_db")
public class Plan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
    private List<Activity> activityList;
    @OneToMany(mappedBy = "plan", cascade = CascadeType.REMOVE)
    private List<VotedPlan> votedPlanList;
    @JoinColumn(name = "id_owner", referencedColumnName = "id")
    @ManyToOne
    private Member owner;
    @JoinColumn(name = "id_city", referencedColumnName = "id")
    @ManyToOne
    private Trip trip;
    @Column(name = "id_status")
    private Integer idStatus;

    @Column(name = "activity_budget")
    private BigDecimal activityBudget;
    @Column(name = "accommodation_budget")
    private BigDecimal accommodationBudget;
    @Column(name = "transportation_budget")
    private BigDecimal transportationBudget;
    @Column(name = "food_budget")
    private BigDecimal foodBudget;

    public Plan() {
    }

    public Plan(Integer id) {
        this.id = id;
    }

}
