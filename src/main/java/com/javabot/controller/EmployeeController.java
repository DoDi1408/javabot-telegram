package com.javabot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


import com.javabot.models.Employee;
import com.javabot.models.Manager;
import com.javabot.models.Response;
import com.javabot.models.Task;
import com.javabot.models.loginForm;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.HashingService;
import com.javabot.serviceimp.ManagerServiceImpl;
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

    @Autowired
    private ManagerServiceImpl managerServiceImpl;

    private final static Logger loggerEmpController = LoggerFactory.getLogger(EmployeeController.class);


    @SuppressWarnings("unused")
    @PostMapping(path = "/register")
    public ResponseEntity<?> completeEmployeeResgistration(@RequestBody loginForm registrationForm) {

        loggerEmpController.info("received a /register");

        if (registrationForm.getTelegramId() == null || registrationForm.getPassword() == null || registrationForm.getEmail() == null) {
            loggerEmpController.error("bad request");
            return ResponseEntity.badRequest().body("Telegram ID, password and email must be provided");
        }

        try {
            Employee employee = employeeServiceImpl.findByTelegramId(Long.valueOf(registrationForm.getTelegramId()).longValue());
            if (employee == null){
                return ResponseEntity.badRequest().body("You need to be previously registerd on the bot");
            }
            if (employee.getPassword() != null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have already registered before");
            }
            String email = registrationForm.getEmail();
            String password = registrationForm.getPassword();

            loggerEmpController.info("hashing password");
            String hashedPassword = hashingService.generateHashFromPassword(password);

            
            employee.setPassword(hashedPassword);
            employee.setEmail(email);

            employeeServiceImpl.update(employee);
            
            employee.setPassword("hidden");

            String jwt = authService.createJWTfromEmployee(employee);

            try {
                Manager manager = managerServiceImpl.findByEmployeeId(employee.getId());
                Response responseBody = new Response(jwt, "manager");
                return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
            }
            catch (NoResultException nre){
                Response responseBody = new Response(jwt, "employee");
                return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
            }

        }
        catch (NoResultException nre){
            loggerEmpController.error("telegram id not found",nre);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("telegram id not found");
        }
        catch (Exception e) {
            loggerEmpController.error("general error",e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @SuppressWarnings("unused")
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

                try {
                    Manager manager = managerServiceImpl.findByEmployeeId(employee.getId());
                    Response responseBody = new Response(jwt, "manager");
                    return ResponseEntity.ok().body(responseBody);
                }
                catch (NoResultException nre){
                    Response responseBody = new Response(jwt, "employee");
                    return ResponseEntity.ok().body(responseBody);
                }
            }
            loggerEmpController.error("error hashing, possibly incorrect password");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect Password");
            
            }
        catch (NoResultException nre){
            loggerEmpController.error("email not found",nre);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("email not found");
        }
        catch (Exception e) {
            loggerEmpController.error("general error",e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @SuppressWarnings({ "unchecked", "null" })
    @GetMapping(path = "/tasks")
    public ResponseEntity<?> getTasksByEmployee(@RequestHeader(value = "token", required = true) String authToken) {
        try {
            ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);
            
            if (employeeResponse.getStatusCode() != HttpStatus.OK){
                return employeeResponse;
            }
            String token = employeeResponse.getHeaders().getFirst("token");

            List<Task> tasks = taskServiceImpl.allEmployeeTasks(employeeResponse.getBody().getId());

            if (tasks.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).header("token", token).build();
            }
            for (Task task : tasks) {
                task.getEmployee().setPassword("hidden");
            }
            return ResponseEntity.status(HttpStatus.OK).header("token", token).body(tasks);
        } 
        catch (Exception e) {
            loggerEmpController.error("general error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @SuppressWarnings({ "unchecked", "null" })
    // doesnt work... for manager cus it has a team, and a manager table... its a mess sadly, i do not know exactly how to do it.
    @DeleteMapping (path = "/deleteUser")
    public ResponseEntity<?> deleteUserEntity(@RequestHeader(value = "token", required = true) String authToken) {
        try {
            ResponseEntity<Employee> employeeResponse = (ResponseEntity<Employee>) authService.getEmployeeFromJWT(authToken);
            
            if (employeeResponse.getStatusCode() != HttpStatus.OK){
                return employeeResponse;
            }

            employeeServiceImpl.delete(employeeResponse.getBody().getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } 
        catch (Exception e) {
            loggerEmpController.error("general error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}