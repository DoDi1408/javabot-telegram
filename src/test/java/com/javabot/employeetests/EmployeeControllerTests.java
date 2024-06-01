package com.javabot.employeetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.javabot.controller.EmployeeController;
import com.javabot.models.Employee;
import com.javabot.models.Response;
import com.javabot.models.Task;
import com.javabot.models.loginForm;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.HashingService;
import com.javabot.serviceimp.TaskServiceImpl;

import jakarta.persistence.NoResultException;

@SpringBootTest
public class EmployeeControllerTests {
    

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private HashingService hashingService;

    @MockBean
    private AuthService authService;

    @MockBean
    private TaskServiceImpl taskService;

    
    @SuppressWarnings("null")
    @Test
    public void testSuccessfulLogin() throws Exception {
        String email = "test@example.com";
        String password = "secret";
        String hashedPassword = "hashedPassword";
        String jwt = "eyJhbGciOiJIUzI1NiJ9...";

        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(hashedPassword);

        Mockito.when(employeeService.findByEmail(email)).thenReturn(employee);
        Mockito.when(hashingService.verifyHash(hashedPassword,password)).thenReturn(true);
        Mockito.when(authService.createJWTfromEmployee(employee)).thenReturn(jwt);

        loginForm loginForm = new loginForm(null, password, email);

        ResponseEntity<?> response = employeeController.employeeLogin(loginForm);
        Response actualResponse = (Response) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Response.class, response.getBody().getClass());
        assertEquals(actualResponse.getJwt(), jwt);
        verify(employeeService, times(1)).findByEmail(email); 
        verify(hashingService, times(1)).verifyHash(hashedPassword,password); 
        verify(authService, times(1)).createJWTfromEmployee(employee); 
    }
    @Test
    public void testErroneousPasswordLogin() throws Exception {
        String email = "test@example.com";
        String password = "secret";
        String hashedPassword = "hashedPassword";

        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(hashedPassword);

        Mockito.when(employeeService.findByEmail(email)).thenReturn(employee);
        Mockito.when(hashingService.verifyHash(hashedPassword,password)).thenReturn(false);

        loginForm loginForm = new loginForm(null, password, email);

        ResponseEntity<?> response = employeeController.employeeLogin(loginForm);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(employeeService, times(1)).findByEmail(email); 
        verify(hashingService, times(1)).verifyHash(hashedPassword,password); 
    }

    @Test
    public void testMissingDataLogin() throws Exception {
        String email = "test@example.com";
        loginForm loginForm = new loginForm(null, null, email);
        ResponseEntity<?> response = employeeController.employeeLogin(loginForm);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email and password must be provided", response.getBody());
    }

    @Test
    public void testEmailNotFoundEmployeeLogin() throws Exception {

        String email = "test@example.com";

        Mockito.when(employeeService.findByEmail(email)).thenThrow(new NoResultException());
        loginForm loginForm = new loginForm(null, "secret",email);

        ResponseEntity<?> response = employeeController.employeeLogin(loginForm);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("email not found", response.getBody());
        verify(employeeService, times(1)).findByEmail(email); 
    }

    @SuppressWarnings("null")
    @Test
    public void testSuccessfulRegistration() throws Exception {
        Long telegramId = 12345L;
        String email = "test@example.com";
        String password = "secret";
        String hashedPassword = "hashedPassword";
        String jwt = "eyJhbGciOiJIUzI1NiJ9...";

        Employee employee = new Employee();
        employee.setTelegramId(telegramId);

        Mockito.when(employeeService.findByTelegramId(telegramId)).thenReturn(employee);
        Mockito.when(hashingService.generateHashFromPassword(password)).thenReturn(hashedPassword);
        Mockito.when(authService.createJWTfromEmployee(employee)).thenReturn(jwt);

        loginForm registrationForm = new loginForm(telegramId.toString(), password, email);

        ResponseEntity<?> response = employeeController.completeEmployeeResgistration(registrationForm);
        Response actualResponse = (Response) response.getBody();
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Response.class, response.getBody().getClass());
        assertEquals(actualResponse.getJwt(), jwt);
        verify(employeeService, times(1)).findByTelegramId(telegramId); 
        verify(hashingService, times(1)).generateHashFromPassword(password); 
        verify(authService, times(1)).createJWTfromEmployee(employee); 
    }
    @Test
    public void testMissingDataRegister() throws Exception {
        loginForm registrationForm = new loginForm("12345", null, "secret");
        ResponseEntity<?> response = employeeController.completeEmployeeResgistration(registrationForm);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Telegram ID, password and email must be provided", response.getBody());
    }
    @Test
    public void testNonExistentTelegramIdRegister() throws Exception {
        Long telegramId = 12345L;
        Mockito.when(employeeService.findByTelegramId(telegramId)).thenThrow(new NoResultException());
        loginForm registrationForm = new loginForm(telegramId.toString(), "secret","test@example.com");

        ResponseEntity<?> response = employeeController.completeEmployeeResgistration(registrationForm);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("telegram id not found", response.getBody());
        verify(employeeService, times(1)).findByTelegramId(telegramId); 
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testGetTasksByEmployee_NoContent() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9..."; 
        Integer employeeId = 1;
        List<Task> tasks = new ArrayList<>(); 

        Employee employee = new Employee();
        employee.setId(employeeId);

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);

        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Mockito.when(taskService.allEmployeeTasks(employeeId)).thenReturn(tasks);

        ResponseEntity<?> response = employeeController.getTasksByEmployee(token);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskService, times(1)).allEmployeeTasks(employeeId);
    }
    
    @Test
    public void testGetTasksByEmployee_InvalidToken() throws Exception {


        String invalidToken = "invalid_token";
        Mockito.when(authService.getEmployeeFromJWT(invalidToken))
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        ResponseEntity<?> response = employeeController.getTasksByEmployee(invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(invalidToken);
    }
}
