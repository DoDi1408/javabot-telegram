package com.javabot;

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

import com.javabot.controller.ManagerController;
import com.javabot.models.Employee;
import com.javabot.models.Manager;
import com.javabot.models.Task;
import com.javabot.models.Team;
import com.javabot.serviceimp.AuthService;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.ManagerServiceImpl;
import com.javabot.serviceimp.TaskServiceImpl;

@SpringBootTest(properties = "spring.main.lazy-initialization=true",classes = {ManagerController.class})
public class ManagerControllerTests {

    @Autowired
    private ManagerController managerController;

    @MockBean
    private AuthService authService;

    @MockBean
    private ManagerServiceImpl managerServiceImpl;

    @MockBean
    private EmployeeServiceImpl employeeService;

    @MockBean
    private TaskServiceImpl taskService;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testValidTeamTasks() throws Exception{
        String jwt = "eyJhbGciOiJIUzI1NiJ9...";
        Integer employeeId = 1;

        List<Task> teamTasks = new ArrayList<>(); 
        teamTasks.add(new Task());
        teamTasks.add(new Task());

        Employee employee = new Employee();
        employee.setId(employeeId);

        teamTasks.get(0).setEmployee(employee);
        teamTasks.get(1).setEmployee(employee);

        Team team = new Team("lemao");
        team.setId(1);

        Manager teamManager = new Manager(employee,team);

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);

        Mockito.when(authService.getEmployeeFromJWT(jwt)).thenReturn((ResponseEntity) entity);
        Mockito.when(managerServiceImpl.findByEmployeeId(employeeId)).thenReturn(teamManager);
        Mockito.when(taskService.allTeamTasks(1)).thenReturn(teamTasks);

        ResponseEntity<?> response = managerController.getTeamTasks(jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teamTasks, response.getBody());

        verify(managerServiceImpl,times(1)).findByEmployeeId(employeeId);
        verify(authService, times(1)).getEmployeeFromJWT(jwt);
        verify(taskService, times(1)).allTeamTasks(1);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testValidNoContentTeamTasks() throws Exception{
        String jwt = "eyJhbGciOiJIUzI1NiJ9...";
        Integer employeeId = 1;

        List<Task> teamTasks = new ArrayList<>(); 

        Employee employee = new Employee();
        employee.setId(employeeId);

        Team team = new Team("lemao");
        team.setId(1);

        Manager teamManager = new Manager(employee,team);

        ResponseEntity<?> entity = new ResponseEntity<>(employee, HttpStatus.OK);

        Mockito.when(authService.getEmployeeFromJWT(jwt)).thenReturn((ResponseEntity) entity);
        Mockito.when(managerServiceImpl.findByEmployeeId(employeeId)).thenReturn(teamManager);
        Mockito.when(taskService.allTeamTasks(1)).thenReturn(teamTasks);

        ResponseEntity<?> response = managerController.getTeamTasks(jwt);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(null, response.getBody());

        verify(managerServiceImpl,times(1)).findByEmployeeId(employeeId);
        verify(authService, times(1)).getEmployeeFromJWT(jwt);
        verify(taskService, times(1)).allTeamTasks(1);
    }

    @SuppressWarnings({ "null", "unchecked", "rawtypes" })
    @Test
    public void testGetTeamTasks_InvalidToken() throws Exception{
        String jwt = "eyJhbGciOiJIUzI1NiJ9...";

        ResponseEntity<?> entity = new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        Mockito.when(authService.getEmployeeFromJWT(jwt)).thenReturn((ResponseEntity) entity);
        ResponseEntity<?> response = managerController.getTeamTasks(jwt);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).getEmployeeFromJWT(jwt);
        
    }
}
