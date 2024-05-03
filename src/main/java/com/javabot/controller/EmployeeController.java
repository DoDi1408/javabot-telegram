package com.javabot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javabot.models.Employee;
import com.javabot.models.Task;
import com.javabot.models.Team;
import com.javabot.serviceimp.EmployeeRepository;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.TeamServiceImpl;

@Controller
@RequestMapping(path = "/employee")
public class EmployeeController {
  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private TeamServiceImpl teamServiceImpl;

  @Autowired
  private EmployeeServiceImpl employeeServiceImpl;

  @PostMapping(path = "/add")
  public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam Integer teamNum) {
    Employee n = new Employee();
    n.setFirstName(name);
    Team t = teamServiceImpl.findById(teamNum);
    n.setTeam(t);
    employeeRepository.save(n);
    System.out.println(t);
    return "Saved Successfully!";
  }

  @GetMapping(path = "/all")
  public @ResponseBody Iterable<Employee> getAllUsers() {
    return employeeRepository.findAll();
  }

  @GetMapping(path = "/{id}/tasks")
  public @ResponseBody Iterable<Task> getTasksByEmployee(@PathVariable Integer id) {
    return employeeServiceImpl.allEmployeeTasks(id);
  }

}