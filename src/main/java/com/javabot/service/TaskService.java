package com.javabot.service;

import java.util.List;

import com.javabot.models.Task;

public interface TaskService extends CommonService<Task> {
    

    List<Task> allEmployeeTasks(Integer id);

    List<Task> allTeamTasks(Integer id);

    List<Task> toDoStateTasks(Integer id, Integer state);

    void deleteAllEmployeeTasks(Integer id);

    Task cleanUpForFront(Task task);
}
