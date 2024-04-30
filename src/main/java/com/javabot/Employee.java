package com.javabot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "EMPLOYEE")
@Entity
@Getter
@Setter
public class Employee {

  @Id
  @Column(name = "ID")
  @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
  private Integer id;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "ID_TEAM")
  private Integer idTeam;

  @Column(name = "TELEGRAM_ID", unique = true)
  private long telegramId;

  @Column(name = "TELEGRAM_USERNAME")
  private String telegramUsername;

  @Column(name = "PASSWORD")
  private String password;
}