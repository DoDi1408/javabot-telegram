package com.javabot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.javabot.controller.TasksController;
import com.javabot.models.Employee;
import com.javabot.models.Task;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.TaskServiceImpl;

@SpringBootTest(properties = "spring.main.lazy-initialization=true",classes = {TasksController.class})
public class TaskControllerTests {

    @Autowired
    private TasksController tasksController;

    @MockBean
    private AuthService authService;

    @MockBean
    private TaskServiceImpl taskServiceImpl;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCreateTask_Success() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9..."; 
        Integer employeeId = 1;
        Task task = new Task();
        task.setTitle("Sample Task");
        task.setDescription("This is a sample task description");
        task.setDueDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24))); 

        Employee employee = new Employee();
        employee.setId(employeeId);

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);

        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Mockito.doNothing().when(taskServiceImpl).create(task);

        ResponseEntity<?> response = tasksController.createTask(token, task);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskServiceImpl, times(1)).create(task);
    }

    @Test
    public void testCreateTask_MissingFields() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9..."; 

        // NO TITLE
        Task task1 = new Task();
        task1.setDescription("This is a sample task description");
        task1.setDueDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));

        // NO DESCRIPTION
        Task task2 = new Task();
        task2.setTitle("Sample Task");
        task2.setDueDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));

        // NO DUE DATE
        Task task3 = new Task();
        task3.setTitle("Sample Task");
        task3.setDescription("This is a sample task description");

        ResponseEntity<?> response1 = tasksController.createTask(token, task1);
        ResponseEntity<?> response2 = tasksController.createTask(token, task2);
        ResponseEntity<?> response3 = tasksController.createTask(token, task3);

        
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, response3.getStatusCode());
        verify(taskServiceImpl, times(0)).create(task1); 
        verify(taskServiceImpl, times(0)).create(task2);
        verify(taskServiceImpl, times(0)).create(task3);
    }


    @Test
    public void testCreateTask_InvalidToken() throws Exception {

        String invalidToken = "invalid_token";
        Task task = new Task();
        task.setTitle("Sample Task");
        task.setDescription("This is a sample task description");
        task.setDueDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24))); 

        Mockito.when(authService.getEmployeeFromJWT(invalidToken)).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        ResponseEntity<?> response = tasksController.createTask(invalidToken, task);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(invalidToken);
        verify(taskServiceImpl, times(0)).create(task); // No task creation for invalid tokens
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateTask_Success() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9..."; 
        Integer employeeId = 1;
        Integer taskId = 2;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Updated Task Title");

        Employee employee = new Employee();
        employee.setId(employeeId);

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);

        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setEmployee(employee);
        Mockito.when(taskServiceImpl.findById(taskId)).thenReturn(existingTask);
        Mockito.doNothing().when(taskServiceImpl).update(task);

        ResponseEntity<?> response = tasksController.updateTask(token, task);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskServiceImpl, times(1)).findById(taskId);
        verify(taskServiceImpl, times(1)).update(task);
    }

    @Test
    public void testUpdateTask_InvalidToken() throws Exception {

        String invalidToken = "invalid_token";

        Integer taskId = 2;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Updated Task Title");


        Mockito.when(authService.getEmployeeFromJWT(invalidToken)).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        ResponseEntity<?> response = tasksController.updateTask(invalidToken, task);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(invalidToken);
        verify(taskServiceImpl, times(0)).findById(taskId);
        verify(taskServiceImpl, times(0)).update(task); 
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testUpdateTask_NonExistentTask() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9...";
        Integer employeeId = 1;
        Integer taskId = 2;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Updated Task Title");

        Employee employee = new Employee();
        employee.setId(employeeId);
        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);

        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Mockito.when(taskServiceImpl.findById(taskId)).thenReturn(null);

        ResponseEntity<?> response = tasksController.updateTask(token, task);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskServiceImpl, times(1)).findById(taskId);
        verify(taskServiceImpl, times(0)).update(task); 
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testUpdateTask_UnauthorizedUpdate() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9...";
        Integer employeeId = 1; 
        Integer taskId = 2;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Updated Task Title");

        Employee employee = new Employee();
        employee.setId(employeeId); 

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);
        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        Employee otherEmployee = new Employee();
        otherEmployee.setId(10);
        existingTask.setEmployee(otherEmployee); 

        Mockito.when(taskServiceImpl.findById(taskId)).thenReturn(existingTask);

        ResponseEntity<?> response = tasksController.updateTask(token, task);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskServiceImpl, times(1)).findById(taskId);
        verify(taskServiceImpl, times(0)).update(task);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testDeleteTask_Success() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9..."; 
        Integer employeeId = 1; 
        Integer taskId = 2;

        Employee employee = new Employee();
        employee.setId(employeeId);

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);
        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setEmployee(employee);
        Mockito.when(taskServiceImpl.findById(taskId)).thenReturn(existingTask);
        Mockito.doNothing().when(taskServiceImpl).delete(taskId);

        ResponseEntity<?> response = tasksController.deleteTask(token, taskId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskServiceImpl, times(1)).findById(taskId);
        verify(taskServiceImpl, times(1)).delete(taskId);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testDeleteTask_UnauthorizedDelete() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9..."; 
        Integer employeeId = 1; 
        Integer taskId = 2;

        Employee employee = new Employee();
        employee.setId(employeeId); 

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);
        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Task existingTask = new Task();
        existingTask.setId(taskId);
        Employee otherEmployee = new Employee();
        otherEmployee.setId(10);
        existingTask.setEmployee(otherEmployee); 

        Mockito.when(taskServiceImpl.findById(taskId)).thenReturn(existingTask);

        ResponseEntity<?> response = tasksController.deleteTask(token, taskId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskServiceImpl, times(1)).findById(taskId);
        verify(taskServiceImpl, times(0)).delete(taskId);
    }
    @Test
    public void testDeleteTask_InvalidToken() throws Exception {

        String invalidToken = "invalid_token";
        Integer taskId = 2;

        Mockito.when(authService.getEmployeeFromJWT(invalidToken)).thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        ResponseEntity<?> response = tasksController.deleteTask(invalidToken, taskId);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(invalidToken);
        verify(taskServiceImpl, times(0)).findById(taskId);
        verify(taskServiceImpl, times(0)).delete(taskId);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testDeleteTask_NonExistentTask() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9...";
        Integer employeeId = 1;
        Integer taskId = 2;

        Employee employee = new Employee();
        employee.setId(employeeId);
        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);
        Mockito.when(authService.getEmployeeFromJWT(token)).thenReturn((ResponseEntity) entity);

        Mockito.when(taskServiceImpl.findById(taskId)).thenReturn(null);

        ResponseEntity<?> response = tasksController.deleteTask(token, taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(token);
        verify(taskServiceImpl, times(1)).findById(taskId);
        verify(taskServiceImpl, times(0)).delete(taskId); 
    }
}

