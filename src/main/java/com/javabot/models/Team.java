package com.javabot.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TEAM")
public class Team {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "team_seq", sequenceName = "team_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq")
    private Integer id;

    @Column(name = "NAME_TEAM")
    private String nameTeam;

    public Team(){

    }

    public Team(String nameTeam) {
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

    @Override
    public String toString() {
        return "Team [id=" + id + ", nameTeam=" + nameTeam + "]";
    }
}
