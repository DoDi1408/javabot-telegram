package com.javabot.models;


public class Response {
    private String jwt;
    private String employeeType;

    public Response(String jwt, String employeeType) {
        this.jwt = jwt;
        this.employeeType = employeeType;
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
