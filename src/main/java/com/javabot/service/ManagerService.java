package com.javabot.service;

import com.javabot.models.Manager;

public interface ManagerService extends CommonService<Manager>{

    void createManager(String firstName, String lastName, String telegramId, String teamName);

    Manager findByEmployeeId(Integer employeeId);

}
