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
import com.javabot.models.Response;
import com.javabot.models.loginForm;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.HashingService;

import jakarta.persistence.NoResultException;

@SpringBootTest
public class EmployeeRegistrationTests {

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private HashingService hashingService;

    @MockBean
    private AuthService authService;

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
    public void testMissingData() throws Exception {
        loginForm registrationForm = new loginForm("12345", null, "secret");
        ResponseEntity<?> response = employeeController.completeEmployeeResgistration(registrationForm);
        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Telegram ID, password and email must be provided", response.getBody());
    }
    @Test
    public void testNonExistentTelegramId() throws Exception {
        Long telegramId = 12345L;
        Mockito.when(employeeService.findByTelegramId(telegramId)).thenThrow(new NoResultException());
        loginForm registrationForm = new loginForm(telegramId.toString(), "secret","test@example.com");

        ResponseEntity<?> response = employeeController.completeEmployeeResgistration(registrationForm);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("telegram id not found", response.getBody());
        verify(employeeService, times(1)).findByTelegramId(telegramId); 
    }
}
