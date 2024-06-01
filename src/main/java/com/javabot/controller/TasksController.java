package com.javabot.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.javabot.models.Employee;
import com.javabot.models.Task;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.TaskServiceImpl;

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

        if (task.getDescription() == null || task.getDueDate()== null || task.getTitle() == null) {
            loggerTasks.error("bad request");
            return ResponseEntity.badRequest().body("You need description, due date, and title.");
        }
        task.setStateTask(0);
        task.setStartDate(new Date());
        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);

            if (employeeResponse.getStatusCode() != HttpStatus.OK){
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
    @PutMapping (path = "/updateTask")
    public ResponseEntity<?> updateTask(@RequestHeader(value = "token", required = true) String authToken,@RequestBody Task task ) {
        loggerTasks.info("received an update task");


        ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);
        if (employeeResponse.getStatusCode() != HttpStatus.OK){
            return employeeResponse;
        }

        try {
            Task theTask = taskServiceImpl.findById(task.getId());
            if (theTask == null){
                loggerTasks.error("task id not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("task not found");
            }
            if (theTask.getEmployee().getId() != employeeResponse.getBody().getId()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the task owner");
            }
            String token = employeeResponse.getHeaders().getFirst("token");
            
            task.setEmployee(employeeResponse.getBody());
            taskServiceImpl.update(task);
            return ResponseEntity.status(HttpStatus.OK).header("token", token).build();
        } 
        catch (Exception e) {
            loggerTasks.error("server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }

    @SuppressWarnings({ "unchecked", "null" })
    @DeleteMapping (path = "/deleteTask/{task_id}")
    public ResponseEntity<?> deleteTask(@RequestHeader(value = "token", required = true) String authToken, @PathVariable Integer task_id){
        loggerTasks.info("received a delete task");
        
        ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);
        if (employeeResponse.getStatusCode() != HttpStatus.OK){
            return employeeResponse;
        }
        try {
            Task theTask = taskServiceImpl.findById(task_id);
            if (theTask == null){
                loggerTasks.error("task id not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("task not found");
            }

            if (theTask.getEmployee().getId() != employeeResponse.getBody().getId()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the task owner");
            }
            String token = employeeResponse.getHeaders().getFirst("token");
            taskServiceImpl.delete(task_id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).header("token", token).build();
        } 
        catch (Exception e) {
            loggerTasks.error("server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }
    
    @SuppressWarnings({ "unchecked", "null" })
    @DeleteMapping (path = "/deleteAllTasks")
    public ResponseEntity<?> deleteAllTasks(@RequestHeader(value = "token", required = true) String authToken){
        loggerTasks.info("received a delete task");
        
        ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);
        if (employeeResponse.getStatusCode() != HttpStatus.OK){
            return employeeResponse;
        }
        try {
            taskServiceImpl.deleteAllEmployeeTasks(employeeResponse.getBody().getId());
            String token = employeeResponse.getHeaders().getFirst("token");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).header("token", token).build();
        } 
        catch (Exception e) {
            loggerTasks.error("server error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }
}
