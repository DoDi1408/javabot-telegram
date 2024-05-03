package com.javabot;

import java.util.Date;
import java.util.List;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

import com.javabot.models.Employee;
import com.javabot.models.Manager;
import com.javabot.models.Task;
import com.javabot.models.Team;
import com.javabot.serviceimp.EmployeeRepository;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.ManagerServiceImpl;
import com.javabot.serviceimp.TaskServiceImpl;
import com.javabot.serviceimp.TeamServiceImpl;
import com.vdurmont.emoji.EmojiParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;



import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

@Component
public class BotHandler {

    private final static Logger loggerHandler = LoggerFactory.getLogger(BotHandler.class);

    @Autowired
    EmployeeRepository EmployeeRepository;

    @Autowired
    ManagerServiceImpl managerServiceImpl;

    @Autowired
    EmployeeServiceImpl employeeServiceImpl;

    @Autowired
    TeamServiceImpl teamServiceImpl;

    @Autowired
    TaskServiceImpl taskServiceImpl;

    public BotHandler(){
    }

    public SendMessage handleStart(long chat_id){
        String startMessage = "Hello! I am a To-Do Bot, I'll be glad to be of use to you!";
        SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(startMessage)
                    .build();
        return message;
    }

    public SendMessage handleRegistrationEmployee(long chat_id, User user){

        Employee emp = new Employee();
        emp.setFirstName(user.getFirstName());
        emp.setLastName(user.getLastName());
        emp.setTelegramId(chat_id);

        try {
            EmployeeRepository.save(emp);
        }
        catch (DataIntegrityViolationException e){
            loggerHandler.error("Seems like it already exists...:", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Oops! Seems like you have already registered before")
                    .build();
            return new_message;
        }
        catch (Exception e){
            loggerHandler.error("General error", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("500: Internal Server Error, sorry :(")
                    .build();
            return new_message;
        }
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Successfully registered you! You can now begin to add tasks!\n You can now complete your registration over at frontend.romongo.uk!\n Remember, your username is " +  chat_id + ".")
                    .build();
        return new_message;
    }

    public SendMessage handleRegistrationManager(long chat_id){
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("To register as a manager send me a message like this: \n REGISTER_MANAGER Your_team_name_without_spaces")
                    .build();
        return new_message;


    }
    // tries to create manager, if employee already exists throws exception
    public SendMessage handleRegistrationManagerReal(long chat_id, User user, String teamName){
        try {
            managerServiceImpl.createManager(user.getFirstName(),user.getLastName(), Long.toString(chat_id), teamName);
        }
        catch (DataIntegrityViolationException e){
            loggerHandler.error("Seems like it already exists...:", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Oops! Seems like you have already registered before")
                    .build();
            return new_message;
        }
        catch (Exception e){
            loggerHandler.error("General error", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("500: Internal Server Error, sorry :(")
                    .build();
            return new_message;
        }
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Successfully registered you! You can now see your team's tasks!\n You can now complete your registration over at frontend.romongo.uk!\n Remember, your username is " +  chat_id + ".")
                    .build();
        return new_message;
    }
    // self explanatory
    public SendMessage handleGetTeams(long chat_id){
        try{
            String textMessage = new String();
            List<Team> teamList = teamServiceImpl.getAllTeams();
            for (Team team : teamList) {
                textMessage = textMessage + team.getId() + " " + team.getNameTeam()+ "\n";
            }
            textMessage = textMessage + "To join a team simply type JOIN_TEAM team_number (This will change your current team)";
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("These are the available teams: \n" + textMessage)
                    .build();
            return new_message;
        }
        catch (Exception e){
            loggerHandler.error("General error", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("500: Internal Server Error, sorry :(")
                    .build();
            return new_message;
        }
    }
    // changes team for employee, if manager doesnt allow it
    public SendMessage handleChangeTeam(long chat_id, String team_num){
        try {
            Employee modifyEmployee = EmployeeRepository.findByTelegramId(chat_id);
            try {
                Manager man = managerServiceImpl.findByEmployeeId(modifyEmployee.getId());
                loggerHandler.info("MANAGER DETECTED, THOU CANT CHANGE TEAM");
                SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("You are a manager! Managers can't change teams.")
                    .build();
                return new_message;
            }
            catch (NoResultException e){
                loggerHandler.error("Manager does not exist (Thats good)", e);
                    Team teamToBeAdded = teamServiceImpl.findById(Integer.valueOf(team_num));
                    if (teamToBeAdded == null){
                        loggerHandler.error("Team does not exist (Thats bad)");
                        SendMessage new_message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("That team does not exist")
                        .build();
                        return new_message;
                    }
                    else {
                        modifyEmployee.setTeam(teamToBeAdded.getId());
                        EmployeeRepository.save(modifyEmployee);
                        SendMessage new_message = SendMessage
                                .builder()
                                .chatId(chat_id)
                                .text("Success!\n You are now a part of team: " + teamToBeAdded.getNameTeam())
                                .build();
                        return new_message;
                    }
            }
        }
        catch (Exception e){
            loggerHandler.error("General error", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("500: Internal Server Error, sorry :(")
                    .build();
            return new_message;

        }
    }
    public SendMessage getTodoList(long chat_id){
        try {
            Employee modifyEmployee = EmployeeRepository.findByTelegramId(chat_id);
            List<Task> todoList = employeeServiceImpl.allEmployeeTasks(modifyEmployee.getId());
            String toDoTask = EmojiParser.parseToUnicode("ToDo tasks: :memo: \n");
            String inProgressTask = EmojiParser.parseToUnicode("InProgress tasks: :hourglass: \n");
            String completedTask = EmojiParser.parseToUnicode("Completed tasks: :white_check_mark: \n");
            for (Task task : todoList) {
                if (task.getStateTask().equals(0)){
                    toDoTask = toDoTask + "- " + task.getId() + " " + task.getDescription()+ " " + task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + " - "  + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                }
                else if (task.getStateTask().equals(1)){
                    inProgressTask = inProgressTask + "- " + task.getId() + " " + task.getDescription()+ " " + task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + " - "  + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                }
                else if (task.getStateTask().equals(2)){
                    completedTask = completedTask + "- " + task.getId() + " " + task.getDescription()+ " " + task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + " - "  + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                }
            }
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(toDoTask + inProgressTask + completedTask)
                    .build();
            return new_message;
        }
        catch (EntityNotFoundException e) {
            loggerHandler.error("Entity not found", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("I did not find you on our internal database, are you sure you area already registered?")
                    .build();
            return new_message;
        }
        catch (Exception e){
            loggerHandler.error("General error", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("500: Internal Server Error, sorry :(")
                    .build();
            return new_message;
        }
    }

    public SendMessage getTodoListTeam(long chat_id){
        try {
            Employee modifyEmployee = EmployeeRepository.findByTelegramId(chat_id);
            try {
                if (modifyEmployee == null){
                    loggerHandler.error("Entity not found");
                    SendMessage new_message = SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text("I did not find you on our internal database, are you sure you area already registered?")
                            .build();
                    return new_message;
                }
                Manager man = managerServiceImpl.findByEmployeeId(modifyEmployee.getId());
                loggerHandler.info("MANAGER DETECTED, YOU CAN!! CHECK ALL TASKS OF YOUR TEAM");

                Integer teamID = man.getTeam().getId();
                List <Task> teamTasks = managerServiceImpl.allTeamTasks(teamID);

                String toDoTask = EmojiParser.parseToUnicode("ToDo tasks: :memo: \n");
                String inProgressTask = EmojiParser.parseToUnicode("InProgress tasks: :hourglass: \n");
                String completedTask = EmojiParser.parseToUnicode("Completed tasks: :white_check_mark: \n");

                for (Task task : teamTasks) {
                    if (task.getStateTask().equals(0)){
                    toDoTask = toDoTask + "- " + task.getDescription()+ " " + task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + " - "  + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                    }
                    else if (task.getStateTask().equals(1)){
                        inProgressTask = inProgressTask + "- " + task.getDescription()+ " " + task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + " - "  + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                    }
                    else if (task.getStateTask().equals(2)){
                        completedTask = completedTask + "- " + task.getDescription()+ " " + task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + " - "  + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                    }
                }
                SendMessage new_message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text(toDoTask + inProgressTask + completedTask)
                        .build();
                return new_message;
            }
            catch (NoResultException notManagerError){
                loggerHandler.error("YOU ARE NOT A MANAGER!!! REEEW", notManagerError);
                SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("You are not a manager! You can't do that!")
                    .build();
                return new_message;
            }
        }
        catch (Exception e){
            loggerHandler.error("General error", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("500: Internal Server Error, sorry :(")
                    .build();
            return new_message;

        }

    }
    public SendMessage handleAddItem(long chat_id){
        String addItem = "To add an item: \n ADD_TASK 2099-12-31 A normal task description";
        SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(addItem)
                    .build();
        return message;
    }

    public SendMessage addTask(long chat_id, String message_text, String[] words){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(words[1]);
            Employee employee = EmployeeRepository.findByTelegramId(chat_id);
            if (employee == null){
                loggerHandler.error("Entity not found");
                SendMessage new_message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("I did not find you on our internal database, are you sure you area already registered?")
                        .build();
                return new_message;
            }
            int firstSpace = message_text.indexOf(' ');
            int secondSpace = message_text.indexOf(' ', firstSpace + 1);

            String description = message_text.substring(secondSpace + 1);
            Date currentTime = new Date();
            Task newTask = new Task(currentTime,date,description,0,employee);
            try {
                taskServiceImpl.create(newTask);
                SendMessage message = SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text("Successfully added a new task!")
                            .build();
                return message;
            }
            catch (Exception e){
                loggerHandler.error("Some unknown error occurred",e);
                SendMessage message = SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text("Internal server error")
                            .build();
                return message;
            }
        }
        catch (ParseException e){
            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Incorrect date format")
                    .build();
            return message;
        }
    }
    public SendMessage handleUpdateTaskCommand(long chat_id){
        String update = "To update a task either type PROCEED_TASK or REVERT_TASK and a valid task ID (You can check those through the /todoList command). \n A Task's Lifecycle looks like this: \n Todo -> InProgress -> Completed";
        SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(update)
                    .build();
        return message;
    }
    public SendMessage handleUpdateTask(long chat_id, Integer task_id, boolean AdvanceOrGoBack){
        Task task = taskServiceImpl.findById(task_id);
        Integer stateTask = task.getStateTask();
        long telegramIdOwner = task.getEmployee().getTelegramId();
        if (telegramIdOwner == chat_id){
            if (AdvanceOrGoBack){
                if (stateTask == 2){
                    SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Completed is the highest stage in a task's lifecycle.")
                        .build();
                    return message;
                }
                else{
                    task.setStateTask(stateTask+1);
                    taskServiceImpl.update(task);
                    SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Advanced task with id " + task.getId())
                        .build();
                    return message;
                }
            }
            else {
                if (stateTask == 0){
                    SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("ToDo is the lowest stage in a task's lifecycle.")
                        .build();
                    return message;
                }
                else{
                    task.setStateTask(stateTask-1);
                    taskServiceImpl.update(task);
                    SendMessage message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Reverted task with id " + task.getId())
                        .build();
                    return message;
                }
            }
        }
        else {
            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("You cant modify a task you don't own.")
                    .build();
            return message;
        }
    }
    public SendMessage handleDeleteCommand (long chat_id){
        String update = "To delete a task type DELETE_TASK and a valid task ID. \n (You can check those through the /todoList command).";
        SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(update)
                    .build();
        return message;
    }

    public SendMessage handleDeleteTask (long chat_id, Integer task_id){
        Task task = taskServiceImpl.findById(task_id);
        long telegramIdOwner = task.getEmployee().getTelegramId();
        if (telegramIdOwner == chat_id){
            try {
                taskServiceImpl.delete(task_id);
                loggerHandler.info("Deleting task with id " + task_id);
                SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Deleted task with id " + task_id)
                    .build();
                return message;
            }
            catch (Exception e){
                loggerHandler.error("Error deleting task", e);
                SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Internal server error.")
                    .build();
                return message;
            }
        }
        else{
            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("You cant delete a task you don't own.")
                    .build();
            return message;
        }
    }

}
