/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "city", schema = "gta_db")
public class Trip implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "start_at")
    private Timestamp startAt;
    @Column(name = "end_at")
    private Timestamp endAt;

    @Column(name = "start_utc_at")
    private Timestamp startUtcAt;
    @Column(name = "end_utc_at")
    private Timestamp endUtcAt;

    @Column(name = "vote_end_at")
    private Timestamp voteEndAt;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE)
    private List<SuggestedActivity> suggestedActivityList;
    @JoinColumn(name = "id_journey", referencedColumnName = "id")
    @ManyToOne
    private Group group;
    @JoinColumn(name = "id_end_place", referencedColumnName = "id")
    @ManyToOne
    private Place endPlace;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE)
    private List<Plan> planList;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.REMOVE)
    private List<Task> taskList;

    @Transient
    private Plan electedPlan;

    public Trip() {
    }

    public Trip(Integer id) {
        this.id = id;
    }

}
