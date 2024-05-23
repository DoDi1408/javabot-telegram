package com.javabot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.javabot.models.Employee;
import com.javabot.models.Task;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.TaskServiceImpl;

import jakarta.persistence.NoResultException;

@CrossOrigin
@Controller
@RequestMapping(path = "/tasks")
public class TasksController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TaskServiceImpl taskServiceImpl;

    private final static Logger loggerTasks = LoggerFactory.getLogger(TasksController.class);

    @PostMapping (path = "/createTask")
    public ResponseEntity<?> createTask(@RequestHeader(value = "token", required = true) String authToken, @RequestBody Task task) {
        loggerTasks.info("received a create task");
        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);

            if (employeeResponse.getStatusCode() != HttpStatus.ACCEPTED){
                return employeeResponse;
            }
            Employee employee = employeeResponse.getBody();
            task.setEmployee(employee);
            taskServiceImpl.create(task);
            String token = employeeResponse.getHeaders().getFirst("token");
            return ResponseEntity.status(HttpStatus.CREATED).header("token", token).build();
            } 
        catch (Exception e) {
            loggerTasks.error("general error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @SuppressWarnings({ "null", "unchecked" })
    @PutMapping (path = "/{taskId}/updateTask")
    public ResponseEntity<?> updateTask(@PathVariable Integer taskId, @RequestParam("state") Integer newState,@RequestHeader(value = "token", required = true) String authToken) {
        loggerTasks.info("received an update task");

        /*
        final Integer ToDo = 0;
        final Integer InProgress = 1;
        final Integer Completed = 2;
        */

        if ((newState < 0) || (newState > 3) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect request, task state must be between 0 and 2");
        }

        ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);
        if (employeeResponse.getStatusCode() != HttpStatus.ACCEPTED){
            return employeeResponse;
        }

        try {
            Task theTask = taskServiceImpl.findById(taskId);
            if (theTask.getEmployee().getId() != employeeResponse.getBody().getId()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the task owner");
            }
            theTask.setStateTask(newState);
            taskServiceImpl.update(theTask);
            return ResponseEntity.ok("Success");
        } 
        catch(NoResultException nre){
            loggerTasks.error("task id not found",nre);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("task not found");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }

}
