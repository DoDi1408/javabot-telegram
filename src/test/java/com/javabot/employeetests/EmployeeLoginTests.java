package com.javabot.employeetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.javabot.controller.EmployeeController;
import com.javabot.models.Employee;
import com.javabot.models.loginForm;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.HashingService;

import jakarta.persistence.NoResultException;

@SpringBootTest
public class EmployeeLoginTests {

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private HashingService hashingService;

    @MockBean
    private AuthService authService;
    
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

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jwt, response.getBody());
        verify(employeeService, times(1)).findByEmail(email); 
        verify(hashingService, times(1)).verifyHash(hashedPassword,password); 
        verify(authService, times(1)).createJWTfromEmployee(employee); 
    }
    @Test
    public void testErroneousPassword() throws Exception {
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
    public void testMissingData() throws Exception {
        String email = "test@example.com";
        loginForm loginForm = new loginForm(null, null, email);
        ResponseEntity<?> response = employeeController.employeeLogin(loginForm);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email and password must be provided", response.getBody());
    }

    @Test
    public void testNotFoundEmployee() throws Exception {

        String email = "test@example.com";

        Mockito.when(employeeService.findByEmail(email)).thenThrow(new NoResultException());
        loginForm loginForm = new loginForm(null, "secret",email);

        ResponseEntity<?> response = employeeController.employeeLogin(loginForm);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("email not found", response.getBody());
        verify(employeeService, times(1)).findByEmail(email); 
    }
}
