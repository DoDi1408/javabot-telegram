package com.javabot.service;

import java.util.List;

import com.javabot.models.Task;

public interface TaskService extends CommonService<Task> {
    //Si quieres todas las tareas del employee o del manager, estos metodos se encuentran Employee y Manager

    List<Task> allEmployeeTasks(Integer id);

    List<Task> allTeamTasks(Integer id);

    List<Task> toDoStateTasks(Integer id, Integer state);
}
