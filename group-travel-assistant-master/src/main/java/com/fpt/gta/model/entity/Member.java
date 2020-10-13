/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.gta.model.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "member", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "Member1.findAll", query = "SELECT m FROM Member m")})

public class Member implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "id_role")
    private Integer idRole;
    @Column(name = "id_status")
    private Integer idStatus;
    @OneToMany(mappedBy = "member")
    private List<VotedSuggestedActivity> votedSuggestedActivityList;
    @OneToMany(mappedBy = "owner")
    private List<SuggestedActivity> suggestedActivityList;
    @JoinColumn(name = "id_joined_journey", referencedColumnName = "id")
    @ManyToOne
    private Group joinedGroup;
    @JoinColumn(name = "id_person", referencedColumnName = "id")
    @ManyToOne
    private Person person;
    @OneToMany(mappedBy = "member")
    private List<VotedPlan> votedPlanList;
    @OneToMany(mappedBy = "owner")
    private List<Plan> planList;
    @OneToMany(mappedBy = "owner")
    private List<Transaction> transactionList;
    @OneToMany(mappedBy = "member")
    private List<TransactionDetail> transactionDetailList;
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<TaskAssignment> taskList;
    @Column(name = "is_ready")
    private Boolean isReady=false;

    @Column(name = "activity_budget")
    private BigDecimal activityBudget = BigDecimal.ZERO;
    @Column(name = "accommodation_budget")
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    @Column(name = "transportation_budget")
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    @Column(name = "food_budget")
    private BigDecimal foodBudget = BigDecimal.ZERO;

    public Member() {
    }

    public Member(Integer id) {
        this.id = id;
    }

}
