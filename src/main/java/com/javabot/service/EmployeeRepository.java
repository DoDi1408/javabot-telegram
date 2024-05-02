package com.javabot.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.javabot.models.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    @Query("SELECT 1")
    void testConnection();

    @Query("SELECT e FROM Employee e WHERE e.telegramId = :telegramId")
    Employee findByTelegramId(@Param("telegramId") long telegramId);
}