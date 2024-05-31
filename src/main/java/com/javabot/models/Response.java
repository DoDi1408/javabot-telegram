package com.javabot.models;

import com.javabot.service.EmployeeService;
import com.javabot.service.ManagerService;

public class Response {
    private String jwt;
    private String email;
    private String employeeType;

    public Response(String jwt, String email, EmployeeService employeeService, ManagerService managerService) {
        this.jwt = jwt;
        this.email = email;
        this.employeeType = determineEmployeeType(email, employeeService, managerService);
    }

    private String determineEmployeeType(String email, EmployeeService employeeService, ManagerService managerService) {
        Employee employee = employeeService.findByEmail(email);
        
        if (employee == null) {
            return "No existe";
        }

        Manager manager = managerService.findByEmployeeId(employee.getId());
        return (manager != null) ? "Manager" : "Employee";
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getEmailToCheckTypeEmployee() {
        return email;
    }

    public void setEmailToCheckTypeEmployee(String email) {
        this.email = email;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }
}
