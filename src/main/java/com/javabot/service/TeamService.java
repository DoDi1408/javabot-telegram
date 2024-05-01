package com.javabot.service;

import java.util.List;

import com.javabot.models.Employee;
import com.javabot.models.Manager;
import com.javabot.models.Team;

public interface TeamService extends CommonService<Team>{

    List<Employee> teamEmployees(Integer id);

    Manager teamManager(Integer id);

}
