package com.javabot.service;

import java.util.List;

import com.javabot.models.Manager;
import com.javabot.models.Task;

public interface ManagerService extends CommonService<Manager>{

    List<Task> allTeamTasks(Integer id);

}
