package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "app_instance", schema = "gta_db")
public class AppInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_instance")
    private String idInstance;

    @JoinColumn(name = "id_person", referencedColumnName = "id")
    @ManyToOne
    private Person person;
}
