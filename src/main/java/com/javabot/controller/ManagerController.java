package com.javabot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.javabot.models.Manager;
import com.javabot.models.Task;
import com.javabot.serviceimp.ManagerServiceImpl;


@CrossOrigin(origins = "https://frontend.romongo.uk",maxAge = 3600)
@Controller
@RequestMapping(path = "/manager")
public class ManagerController {

  private final static Logger loggerManager = LoggerFactory.getLogger(ManagerController.class);

  @Autowired
  private ManagerServiceImpl managerServiceImpl;

  
  @GetMapping(path = "/findByEmployeeId")
  public ResponseEntity<Manager> getByEmployeeId(@RequestParam("employee_id") Integer employeeId) {
    loggerManager.info("Received findEyEmployeeId");
    try {
      Manager manager = managerServiceImpl.findByEmployeeId(employeeId);
      if (manager != null) {
        return ResponseEntity.ok(manager);
      } 
      else {
        return ResponseEntity.notFound().build();
      }
    } 
    catch (Exception e) {
      loggerManager.error("An error ocurred: ",e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  // obten team_id de hacer el get anterior
  @GetMapping(path = "/{team_id}/teamTasks")
  public ResponseEntity<Iterable<Task>> getTeamTasks(@PathVariable Integer team_id) {
    try {
      Iterable<Task> tasks = managerServiceImpl.allTeamTasks(team_id);
      return ResponseEntity.ok(tasks);
    } 
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}