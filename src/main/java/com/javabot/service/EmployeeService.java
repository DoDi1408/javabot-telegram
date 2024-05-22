package com.javabot.service;

import java.util.List;

import com.javabot.models.Employee;

public interface EmployeeService extends CommonService<Employee>{

    Employee findByTelegramId(long telegramId);

    Employee findByEmail(String email);
    
    List<Employee> findAll();
}
