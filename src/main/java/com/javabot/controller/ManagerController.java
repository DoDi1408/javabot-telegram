package com.javabot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javabot.models.Employee;
import com.javabot.models.Manager;
import com.javabot.models.Task;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.ManagerServiceImpl;
import com.javabot.serviceimp.TaskServiceImpl;

import jakarta.persistence.NoResultException;


@CrossOrigin
@Controller
@RequestMapping(path = "/manager")
public class ManagerController {

  private final static Logger loggerManager = LoggerFactory.getLogger(ManagerController.class);

  @Autowired
  private ManagerServiceImpl managerServiceImpl;
  
  @Autowired
  private TaskServiceImpl taskServiceImpl;
  
  @Autowired
  private AuthService authService;

  @SuppressWarnings({ "unchecked", "null" })
  @GetMapping(path = "/teamTasks")
  public ResponseEntity<?> getTeamTasks(@RequestHeader(value = "token", required = true) String authToken) {
    loggerManager.info("Received getTeamTasks");
    try {
    	ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);

      	if (employeeResponse.getStatusCode() != HttpStatus.OK){
        	return employeeResponse;
      	}
	
	String token = employeeResponse.getHeaders().getFirst("token");
    try {
        Manager teamManager = managerServiceImpl.findByEmployeeId(employeeResponse.getBody().getId());
        List<Task> tasks = taskServiceImpl.allTeamTasks(teamManager.getTeam().getId());

		if (tasks.isEmpty()){
			return ResponseEntity.status(HttpStatus.NO_CONTENT).header("token", token).build();
		}
        for (Task task : tasks) {
        	task.getEmployee().setPassword("hidden");
        }
        return ResponseEntity.status(HttpStatus.OK).header("token", token).body(tasks);
    }
    catch (NoResultException nre){
        loggerManager.error("not a manager", nre);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not a manager");
    }
    }
    catch (Exception e){
      	loggerManager.error("general error",e);
      	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}