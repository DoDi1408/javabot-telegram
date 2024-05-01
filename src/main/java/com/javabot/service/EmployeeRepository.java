package com.javabot.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.javabot.models.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    @Query("SELECT 1")
    void testConnection();
}