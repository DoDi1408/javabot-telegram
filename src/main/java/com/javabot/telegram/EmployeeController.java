package com.javabot.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/employee")
public class EmployeeController {
  @Autowired
  private EmployeeRepository employeeRepository;

  @PostMapping(path="/add")
  public @ResponseBody String addNewUser (@RequestParam String name, @RequestParam Integer teamNum) {
    Employee n = new Employee();
    n.setFirstName(name);
    n.setIdTeam(teamNum);
    employeeRepository.save(n);
    return "Saved Successfully!";
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<Employee> getAllUsers() {
    return employeeRepository.findAll();
  }
}