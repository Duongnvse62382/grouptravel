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
 * @author ThienNT
 */
@Data
@Entity
@Table(name = "person", schema = "gta_db")
@NamedQueries({
        @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p")})
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "firebase_uid")
    private String firebaseUid;
    @Column(name = "photo_uri")
    private String photoUri;
    @Column(name = "id_status")
    private Integer idStatus;
    @OneToMany(mappedBy = "person")
    private List<Member> memberList;
    @OneToMany(mappedBy = "person")
    private List<AppInstance> appInstanceList;



    public Person() {
    }

    public Person(Integer id) {
        this.id = id;
    }

}
