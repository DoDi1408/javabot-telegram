package com.javabot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



import com.javabot.models.Task;
import com.javabot.serviceimp.TaskServiceImpl;


@Controller
@RequestMapping(path = "/tasks")
public class TasksController {
    
    @Autowired
    private TaskServiceImpl taskServiceImpl;

    private final static Logger loggerTasks = LoggerFactory.getLogger(TasksController.class);

    @PostMapping (path = "/createTask")
    public ResponseEntity<String> createTask(@RequestBody Task task) {
        try {
            loggerTasks.info(task.toString());
            taskServiceImpl.create(task);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            loggerTasks.error("Some error ocurred",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }
    @PutMapping (path = "/{taskId}/updateTask")
    public ResponseEntity<String> updateTask(@PathVariable Integer taskId, @RequestParam("state") Integer newState) {
        final Integer ToDo = 0;
        final Integer InProgress = 1;
        final Integer Completed = 2;
        if ((newState < 0) || (newState > 3) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect request");
        }
        try {
            Task theTask = taskServiceImpl.findById(taskId);
            theTask.setStateTask(newState);
            taskServiceImpl.update(theTask);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }

}
