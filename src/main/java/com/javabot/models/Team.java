package com.javabot.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TEAM")
public class Team {

    @Id
    @Column
    @SequenceGenerator(name = "team_seq", sequenceName = "team_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq")
    private Integer id;
    
    @Column(name = "NAME_TEAM")
    private String nameTeam;

    @OneToMany(mappedBy="team", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    private List<Employee> employees;

    @OneToOne(mappedBy="team", cascade=CascadeType.ALL, orphanRemoval=true)
    private Manager manager;

    public Team(){

    }

    public Team(Integer id, String nameTeam) {
        this.nameTeam = nameTeam;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameTeam() {
        return nameTeam;
    }

    public void setNameTeam(String nameTeam) {
        this.nameTeam = nameTeam;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "Team [id=" + id + ", nameTeam=" + nameTeam + "\n" + employees + "\n" + manager + "]";
    }
}
