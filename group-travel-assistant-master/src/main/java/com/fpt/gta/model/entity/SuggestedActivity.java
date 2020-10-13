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
import java.util.List;

/**
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "suggested_activity", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "SuggestedActivity.findAll", query = "SELECT s FROM SuggestedActivity s")})

public class SuggestedActivity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "id_type")
    private Integer idType;
    @OneToMany(mappedBy = "suggestedActivity", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<VotedSuggestedActivity> votedSuggestedActivityList;
    @JoinColumn(name = "id_owner", referencedColumnName = "id")
    @ManyToOne
    private Member owner;
    @JoinColumn(name = "id_end_place", referencedColumnName = "id")
    @ManyToOne
    private Place endPlace;
    @JoinColumn(name = "id_start_place", referencedColumnName = "id")
    @ManyToOne
    private Place startPlace;
    @JoinColumn(name = "id_city", referencedColumnName = "id")
    @ManyToOne
    private Trip trip;
    @Column(name = "is_too_far")
    private Boolean isTooFar = false;

    public SuggestedActivity() {
    }

    public SuggestedActivity(Integer id) {
        this.id = id;
    }

}
