package com.javabot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "MANAGER")
public class Manager {

    @Id
    @SequenceGenerator(name = "manager_seq", sequenceName = "manager_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manager_seq")
    @Column(name="ID")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID_TEAM", referencedColumnName="id")
    private Team team;

    @OneToOne(optional=false)
    @JoinColumn(name="ID_EMPLOYEE", unique=true, nullable=false, updatable=false, referencedColumnName="id")
    private Employee selfEmployee;


    public Manager(Employee emp, Team team) {
        this.selfEmployee = emp;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
 
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setEmployee(Employee sEmployee){
        this.selfEmployee = sEmployee;
    }

    public Employee getEmployee(){
        return this.selfEmployee;
    }
    @Override
    public String toString() {
        return "Manager [id=" + id +"]";
    }

    

}
