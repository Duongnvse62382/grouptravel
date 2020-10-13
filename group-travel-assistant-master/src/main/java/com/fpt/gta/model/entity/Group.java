/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.gta.model.entity;

import com.fpt.gta.model.constant.GroupStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "journey", schema = "gta_db")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "start_at")
    private Timestamp startAt;
    @Column(name = "end_at")
    private Timestamp endAt;

    @Column(name = "start_utc_at")
    private Timestamp startUtcAt;
    @Column(name = "end_utc_at")
    private Timestamp endUtcAt;

    @Column(name = "invitation_code")
    private String invitationCode;
    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private List<Trip> tripList;
    @OneToMany(mappedBy = "joinedGroup", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Member> memberList;
    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private List<Document> documentList;
    @JoinColumn(name = "id_start_place", referencedColumnName = "id")
    @ManyToOne
    private Place startPlace;
    @JoinColumn(name = "id_currency", referencedColumnName = "id")
    @ManyToOne
    private Currency currency;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private List<Task> taskList;

    @Column(name = "id_status")
    private Integer idStatus = GroupStatus.PLANNING;

    @Column(name = "activity_budget")
    private BigDecimal activityBudget = BigDecimal.ZERO;
    @Column(name = "accommodation_budget")
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    @Column(name = "transportation_budget")
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    @Column(name = "food_budget")
    private BigDecimal foodBudget = BigDecimal.ZERO;

    public Group() {
    }

    public Group(Integer id) {
        this.id = id;
    }

}
