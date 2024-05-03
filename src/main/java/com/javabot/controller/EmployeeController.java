package com.javabot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.javabot.models.Employee;
import com.javabot.models.Task;
import com.javabot.serviceimp.EmployeeRepository;
import com.javabot.serviceimp.EmployeeServiceImpl;

@CrossOrigin(origins = "https://frontend.romongo.uk",maxAge = 3600)
@Controller
@RequestMapping(path = "/employee")
public class EmployeeController {
  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private EmployeeServiceImpl employeeServiceImpl;

  
  @GetMapping(path = "/findByTelegramId")
  public ResponseEntity<Employee> getByTelegramId(@RequestParam("telegram_id") long telegramId) {
    try {
      Employee employee = employeeRepository.findByTelegramId(telegramId);
      if (employee != null) {
        return ResponseEntity.ok(employee);
      } 
      else {
        return ResponseEntity.notFound().build();
      }
    } 
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping(path = "/all")
  public ResponseEntity<Iterable<Employee>> getAllUsers() {
    try {
      Iterable<Employee> employees = employeeRepository.findAll();
      return ResponseEntity.ok(employees);
    } 
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping(path = "/{id}/tasks")
  public ResponseEntity<Iterable<Task>> getTasksByEmployee(@PathVariable Integer id) {
    try {
      Iterable<Task> tasks = employeeServiceImpl.allEmployeeTasks(id);
      return ResponseEntity.ok(tasks);
    } 
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}