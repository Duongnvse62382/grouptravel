package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "task", schema = "gta_db")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "id_status")
    private Integer idStatus;
    @Column(name = "order")
    private Integer order;
    @JoinColumn(name = "id_journey", referencedColumnName = "id")
    @ManyToOne
    private Group group;
    @JoinColumn(name = "id_city", referencedColumnName = "id")
    @ManyToOne
    private Trip trip;
    @JoinColumn(name = "id_activity", referencedColumnName = "id")
    @ManyToOne
    private Activity activity;
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<TaskAssignment> taskAssignmentList;
}
