package com.javabot.models;

import com.javabot.service.ManagerService;

import jakarta.persistence.NoResultException;

public class Response {
    private String jwt;
    private String employeeType;

    public Response(String jwt, Integer employeeId, ManagerService managerService) {
        this.jwt = jwt;
        this.employeeType = determineEmployeeType(employeeId, managerService);
    }

    @SuppressWarnings("unused")
    private String determineEmployeeType(Integer employeeId, ManagerService managerService) {
        try {
            Manager manager = managerService.findByEmployeeId(employeeId);
            return "manager";
        }
        catch (NoResultException nre){
            return "employee";
        }
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }
}
