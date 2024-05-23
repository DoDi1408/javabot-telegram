package com.javabot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


import com.javabot.models.Employee;
import com.javabot.models.Task;
import com.javabot.models.loginForm;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.HashingService;
import com.javabot.serviceimp.TaskServiceImpl;

import jakarta.persistence.NoResultException;

@CrossOrigin
@Controller
@RequestMapping(path = "/employee")
public class EmployeeController {

  @Autowired
  private EmployeeServiceImpl employeeServiceImpl;

  @Autowired
  private TaskServiceImpl taskServiceImpl;

  @Autowired
  private AuthService authService;
  
  @Autowired
  private HashingService hashingService;

  private final static Logger loggerEmpController = LoggerFactory.getLogger(EmployeeController.class);


  @PostMapping(path = "/register")
  public ResponseEntity<?> completeEmployeeResgistration(@RequestBody loginForm registrationForm) {

    loggerEmpController.info("received a /register");

    if (registrationForm.getTelegramId() == null || registrationForm.getPassword() == null || registrationForm.getEmail() == null) {
      loggerEmpController.error("bad request");
      return ResponseEntity.badRequest().body("Telegram ID, password and email must be provided");
    }

    try {
      Employee employee = employeeServiceImpl.findByTelegramId(Long.valueOf(registrationForm.getTelegramId()).longValue());

      String email = registrationForm.getEmail();
      String password = registrationForm.getPassword();

      loggerEmpController.info("hashing password");
      String hashedPassword = hashingService.generateHashFromPassword(password);

      
      employee.setPassword(hashedPassword);
      employee.setEmail(email);

      employeeServiceImpl.update(employee);
      
      employee.setPassword("hidden");

      String jwt = authService.createJWTfromEmployee(employee);
      return ResponseEntity.status(HttpStatus.CREATED).body(jwt);

    }
    catch (NoResultException nre){
      loggerEmpController.error("telegram id not found",nre);
      return ResponseEntity.notFound().build();
    }
    catch (Exception e) {
      loggerEmpController.error("general error",e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping(path = "/login")
  public ResponseEntity<?> employeeLogin(@RequestBody loginForm loginForm) {

    loggerEmpController.info("received a /login");

    if (loginForm.getPassword() == null || loginForm.getEmail() == null) {
      loggerEmpController.error("bad request");
      return ResponseEntity.badRequest().body("Email and password must be provided");
    }

    try {
      Employee employee = employeeServiceImpl.findByEmail(loginForm.getEmail());
      String hashedPassword = employee.getPassword();
      String password = loginForm.getPassword();

      if (hashingService.verifyHash(hashedPassword, password)){
        loggerEmpController.info("password hash verified");
        employee.setPassword("hidden");
        String jwt = authService.createJWTfromEmployee(employee);
        return ResponseEntity.ok().body(jwt);
      }
      loggerEmpController.error("error hashing, possibly incorrect password");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      
    }
    catch (NoResultException nre){
      loggerEmpController.error("email not found",nre);
      return ResponseEntity.notFound().build();
    }
    catch (Exception e) {
      loggerEmpController.error("general error",e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping(path = "/tasks")
  public ResponseEntity<?> getTasksByEmployee(@RequestHeader(value = "token", required = true) String authToken) {
    try {
      @SuppressWarnings("unchecked")
      ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);
      
      if (employeeResponse.getStatusCode() == HttpStatus.ACCEPTED){
        @SuppressWarnings("null")
        Iterable<Task> tasks = taskServiceImpl.allEmployeeTasks(employeeResponse.getBody().getId());
        for (Task task : tasks) {
          task.getEmployee().setPassword("hidden");
        }
        String token = employeeResponse.getHeaders().getFirst("token");
        return ResponseEntity.status(HttpStatus.OK).header("token", token).body(tasks);
      }
      return employeeResponse;
    } 
    catch (Exception e) {
      loggerEmpController.error("general error", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}