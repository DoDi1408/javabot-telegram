package com.javabot.models;

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
    private Integer id;

    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "DUE_DATE")
    private Date dueDate;

    @Column(name="DESCRIPTION_TASK")
    private String description;

    @Column(name="STATE_TASK")
    private Integer stateTask;


    @ManyToOne
    @JoinColumn(name="ID_EMPLOYEE", referencedColumnName="ID")
    private Employee employee;

    public Task(){
        
    }

    public Task(Date startDate, Date dueDate, String description, Integer stateTask, Employee employee) {
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.description = description;
        this.stateTask = stateTask;
        this.employee = employee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStateTask() {
        return stateTask;
    }

    public void setStateTask(Integer stateTask) {
        this.stateTask = stateTask;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", startDate=" + startDate + ", dueDate=" + dueDate + ", description=" + description
                + ", stateTask=" + stateTask + ", employee=" + employee + "]";
    }
}
