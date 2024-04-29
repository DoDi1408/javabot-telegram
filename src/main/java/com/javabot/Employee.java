package com.javabot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Employee {

  @Id
  @Column(name = "ID")
  @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
  private Integer id;

  private String first_name;

  private Integer id_team;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFirstName() {
    return first_name;
  }

  public void setFirstName(String first_name) {
    this.first_name = first_name;
  }

  public Integer getEmail() {
    return id_team;
  }

  public void setIdTeam(Integer idTeam) {
    this.id_team = idTeam;
  }
}