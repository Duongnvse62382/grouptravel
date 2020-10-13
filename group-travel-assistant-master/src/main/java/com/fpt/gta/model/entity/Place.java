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
@Table(name = "place", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "Place.findAll", query = "SELECT p FROM Place p")})
public class Place implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "google_place_id")
    private String googlePlaceId;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "local_name")
    private String localName;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "latitude")
    private BigDecimal latitude;
    @Column(name = "longitude")
    private BigDecimal longitude;
    @Column(name = "estimate_spending_hour")
    private Integer estimateSpendingHour;
    @Column(name = "time_zone")
    private String timeZone;
    @OneToMany(mappedBy = "endPlace")
    private List<Activity> activityList;
    @OneToMany(mappedBy = "startPlace")
    private List<Activity> activityList1;
    @OneToMany(mappedBy = "place")
    private List<OpeningHour> openingHourList;
    @OneToMany(mappedBy = "place")
    private List<PlaceWithType> placeWithTypeList;
    @OneToMany(mappedBy = "endPlace")
    private List<SuggestedActivity> suggestedActivityList;
    @OneToMany(mappedBy = "startPlace")
    private List<SuggestedActivity> suggestedActivityList1;
    @OneToMany(mappedBy = "endPlace")
    private List<Trip> tripList;
    @OneToMany(mappedBy = "place")
    private List<PlaceImage> placeImageList;
    @OneToMany(mappedBy = "startPlace")
    private List<Group> groupList;
    @Transient
    private Boolean isTooFar = false;

    public Place() {
    }

    public Place(Integer id) {
        this.id = id;
    }

}
