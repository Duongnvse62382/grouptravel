package com.fpt.gta.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "task_assignment", schema = "gta_db")
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "id_task", referencedColumnName = "id")
    @ManyToOne
    private Task task;

    @JoinColumn(name = "id_member", referencedColumnName = "id")
    @ManyToOne
    private Member member;
}
