package com.javabot.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "TASK")
public class Task {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    private int id;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "DUE_DATE")
    private Date dueDate;

    @Column(name="DESCRIPTION_TASK")
    private String description;

    @Column(name="STATE_TASK")
    private Integer stateTask;

    @Column(name="SHOW_TASK")
    private Integer showTask;

    @ManyToOne
    @JoinColumn(name="ID_EMPLOYEE", referencedColumnName="id")
    private Employee employee;
}
