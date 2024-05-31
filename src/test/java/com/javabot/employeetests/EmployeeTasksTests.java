package com.javabot.employeetests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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
import com.javabot.models.Task;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.TaskServiceImpl;

@SpringBootTest
public class EmployeeTasksTests {
    
    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private AuthService authService;

    @MockBean
    private TaskServiceImpl taskService;


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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testGetTasksByEmployee_Content() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9..."; 
        Integer employeeId = 1;
        List<Task> tasks = new ArrayList<>(); 
        tasks.add(new Task());

        Employee employee = new Employee();
        employee.setId(employeeId);

        ResponseEntity<Employee> entity = new ResponseEntity<>(employee, HttpStatus.OK);

        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Mockito.when(taskService.allEmployeeTasks(employeeId)).thenReturn(tasks);

        ResponseEntity<?> response = employeeController.getTasksByEmployee(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
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
