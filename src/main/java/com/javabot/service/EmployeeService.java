package com.javabot.service;

import java.util.List;

import com.javabot.models.Employee;
import com.javabot.models.Task;

public interface EmployeeService extends CommonService<Employee>{

    List<Task> allEmployeeTasks(Integer id);

}
