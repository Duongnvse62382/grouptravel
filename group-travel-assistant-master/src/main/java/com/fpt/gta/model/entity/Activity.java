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
@Table(name = "activity", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "Activity.findAll", query = "SELECT a FROM Activity a")})
public class Activity implements Serializable {

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

    @Column(name = "id_type")
    private Integer idType;

    @JoinColumn(name = "id_end_place", referencedColumnName = "id")
    @ManyToOne
    private Place endPlace;
    @JoinColumn(name = "id_start_place", referencedColumnName = "id")
    @ManyToOne
    private Place startPlace;
    @JoinColumn(name = "id_plan", referencedColumnName = "id")
    @ManyToOne
    private Plan plan;
    @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
    private List<DocumentWithActivity> documentWithActivityList;
    @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
    private List<Task> taskList;

    @Transient
    private List<Document> documentList;
//    @Transient
    @Column(name = "is_too_far")
    private Boolean isTooFar = false;

    public Activity() {
    }

    public Activity(Integer id) {
        this.id = id;
    }

}
