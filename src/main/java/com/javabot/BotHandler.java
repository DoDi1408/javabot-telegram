package com.javabot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import com.javabot.models.Employee;
import com.javabot.models.Manager;
import com.javabot.models.Task;
import com.javabot.models.Team;
import com.javabot.serviceimp.EmployeeServiceImpl;
import com.javabot.serviceimp.ManagerServiceImpl;
import com.javabot.serviceimp.TaskServiceImpl;
import com.javabot.serviceimp.TeamServiceImpl;
import com.javabot.util.BotCommands;
import com.vdurmont.emoji.EmojiParser;



import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

@Component
public class BotHandler {

    private final static Logger loggerHandler = LoggerFactory.getLogger(BotHandler.class);

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
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(startMessage)
                    .build();
        return new_message;
    }

    public SendMessage handleRegistration(long chat_id){
        SendMessage new_message = SendMessage
        .builder()
        .chatId(chat_id)
        .text("Choose how you want to register: ")
        .build();

        InlineKeyboardRow row = new InlineKeyboardRow();
    
        InlineKeyboardButton button1 = new InlineKeyboardButton("As Employee");
        button1.setCallbackData(BotCommands.REGISTER_EMP_COMMAND.getCommand());
        row.add(button1);
    
        InlineKeyboardButton button2 = new InlineKeyboardButton("As Manager");
        button2.setCallbackData(BotCommands.REGISTER_MAN_COMMAND.getCommand());
        row.add(button2);
    
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(row);
    
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        new_message.setReplyMarkup(inlineKeyboardMarkup);

        return new_message;
    }

    public SendMessage handleRegistrationEmployee(long chat_id, User user){

        Employee emp = new Employee();
        emp.setFirstName(user.getFirstName());
        emp.setLastName(user.getLastName());
        emp.setTelegramId(chat_id);

        try {
            employeeServiceImpl.create(emp);
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

    public SendMessage handleRegistrationManager(long chat_id, Map<Long , String> userStatesMap){
        userStatesMap.put(chat_id, "REGISTER_MANAGER");
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Ok, now send me a team name.")
                    .build();
        return new_message;


    }
    // tries to create manager, if employee already exists throws exception  
    public SendMessage handleRegistrationManagerReal(long chat_id, User user, String teamName, Map<Long , String> userStatesMap){
        userStatesMap.remove(chat_id);
        try {
            managerServiceImpl.createManager(user.getFirstName(),user.getLastName(), Long.toString(chat_id), teamName);
        }
        catch (DataIntegrityViolationException e){
            loggerHandler.error("Seems like it already exists...:", e);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Oops! Seems like you have already registered before.")
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

            List<InlineKeyboardRow> rows = new ArrayList<>();
            for (Team team : teamList) {
                textMessage = textMessage + team.getId() + " " + team.getNameTeam()+ "\n";
                InlineKeyboardRow row = new InlineKeyboardRow();
                InlineKeyboardButton button = new InlineKeyboardButton(team.getNameTeam());
                button.setCallbackData(String.format("JOIN_TEAM %d",team.getId()));
                row.add(button);
                rows.add(row);
            }
            textMessage = textMessage + "To join a team simply touch one of the teams!";
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("These are the available teams: \n" + textMessage)
                    .replyMarkup(inlineKeyboardMarkup)
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
            Employee modifyEmployee = employeeServiceImpl.findByTelegramId(chat_id);
            try {
                Manager man = managerServiceImpl.findByEmployeeId(modifyEmployee.getId());
                loggerHandler.info(man.toString());
                loggerHandler.info("MANAGER DETECTED, THOU CANT CHANGE TEAM");
                SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("You are a manager! Managers can't change teams.")
                    .build();
                return new_message;
            }
            catch (NoResultException e){
                loggerHandler.info("Manager does not exist (Thats good)", e);
                Team teamToBeAdded = teamServiceImpl.findById(Integer.valueOf(team_num));
                modifyEmployee.setTeam(teamToBeAdded.getId());
                employeeServiceImpl.update(modifyEmployee);
                SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Success!\n You are now a part of team: " + teamToBeAdded.getNameTeam())
                    .build();
                return new_message;  
            }
        }
        catch (NoResultException nre){
            loggerHandler.error("not registered error", nre);
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("You have not registered yet")
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

    public Object getTodoListTeam(long chat_id, Integer messageId){
        try {
            Employee modifyEmployee = employeeServiceImpl.findByTelegramId(chat_id);
            try {
                Manager manager = managerServiceImpl.findByEmployeeId(modifyEmployee.getId());
                loggerHandler.info("MANAGER DETECTED, YOU CAN!! CHECK ALL TASKS OF YOUR TEAM");
                
                Integer teamID = manager.getTeam().getId();
                List<Employee> allEmployees = teamServiceImpl.teamEmployees(teamID);
                List <Task> teamTasks = taskServiceImpl.allTeamTasks(teamID);

                String toDoTask = EmojiParser.parseToUnicode("ToDo tasks: :memo: \n");
                String inProgressTask = EmojiParser.parseToUnicode("InProgress tasks: :hourglass: \n");
                String completedTask = EmojiParser.parseToUnicode("Completed tasks: :white_check_mark: \n");

                for (Task task : teamTasks) {

                    String dueDate = task.getDueDate() != null ? task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : "No due date";

                    if (task.getStateTask().equals(0)){
                        toDoTask = toDoTask + "- " + task.getTitle()+ " | " + dueDate + "\n";
                    }
                    else if (task.getStateTask().equals(1)){
                        inProgressTask = inProgressTask + "- " + task.getTitle()+ " | " + dueDate + "\n";
                    }
                    else if (task.getStateTask().equals(2)){
                        completedTask = completedTask + "- " + task.getTitle()+ " | " + dueDate + "\n";
                    }
                }

                List<InlineKeyboardRow> rows = new ArrayList<>();

                for(Employee employee : allEmployees){
                    InlineKeyboardRow row = new InlineKeyboardRow();
                    InlineKeyboardButton button = new InlineKeyboardButton(employee.getFirstName());
                    button.setCallbackData(BotCommands.GET_EMPLOYEE_TASK.getCommand() + " " + employee.getId());
                    row.add(button);
                    rows.add(row);
                }
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);

                if (messageId == null) {
                    return SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text(toDoTask + inProgressTask + completedTask)
                            .replyMarkup(inlineKeyboardMarkup)
                            .build();
                } else { 
                    return EditMessageText
                            .builder()
                            .chatId(chat_id)
                            .messageId(messageId)
                            .text(toDoTask + inProgressTask + completedTask)
                            .replyMarkup(inlineKeyboardMarkup)
                            .build();
                }
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
        } catch (EntityNotFoundException e) {
            loggerHandler.error("Entity not found", e);
        } catch (Exception e) {
            loggerHandler.error("General error", e);
        }
        return null;
    }

    public EditMessageText handleSendEmployeeTask(long chat_id, Integer employee_id, Integer message_id){
        try {
            List<Task> toDoList = taskServiceImpl.allEmployeeTasks(employee_id);
            String toDoTask = EmojiParser.parseToUnicode("ToDo tasks: :memo: \n");
            String inProgressTask = EmojiParser.parseToUnicode("InProgress tasks: :hourglass: \n");
            String completedTask = EmojiParser.parseToUnicode("Completed tasks: :white_check_mark: \n");
    
            for (Task task : toDoList) {
                String dueDate = task.getDueDate() != null ? task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : "No due date";

                if (task.getStateTask().equals(0)){
                    toDoTask = toDoTask + "- " + task.getTitle() + " | Due: " + dueDate+ "\n";
                } else if (task.getStateTask().equals(1)){
                    inProgressTask = inProgressTask + "- " + task.getTitle() + " | Due: " + dueDate + "\n";
                } else if (task.getStateTask().equals(2)){
                    completedTask = completedTask + "- " + task.getTitle() + " | Due: " + dueDate + "\n";
                }
            }
            List<InlineKeyboardRow> rows = new ArrayList<>();
            InlineKeyboardRow rowBackButton = new InlineKeyboardRow();
            InlineKeyboardButton buttonBack = new InlineKeyboardButton("<< Back to All Team Tasks");
            buttonBack.setCallbackData(BotCommands.TEAM_LIST_COMMAND.getCommand());
            rowBackButton.add(buttonBack);
            rows.add(rowBackButton);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);

            EditMessageText new_message = EditMessageText
            .builder()
            .chatId(chat_id)
            .messageId(message_id)
            .text(toDoTask + inProgressTask + completedTask)
            .replyMarkup(inlineKeyboardMarkup)
            .build();

            return new_message;
        }
        catch (EntityNotFoundException e) {
            loggerHandler.error("Entity not found", e);
        } catch (Exception e) {
            loggerHandler.error("General error", e);
        }
        return null;
    }
    
    public SendMessage handleAddTask(long chat_id, Map<Long , String> userStatesMap){
        userStatesMap.put(chat_id, "ADDING_TASK");
        String addItem = "To add a task simply send me a task description, a title, and a due date and start date";
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(addItem)
                    .build();
        return new_message;
    }

    @SuppressWarnings("null")
    public SendMessage addTask(long chat_id, String message_text, Map<Long , String> userStatesMap){
        userStatesMap.remove(chat_id);
        Employee employee = employeeServiceImpl.findByTelegramId(chat_id);
        if (employee == null){
            loggerHandler.error("Entity not found");
            SendMessage new_message = SendMessage
                .builder()
                .chatId(chat_id)
                .text("I did not find you on our internal database, are you sure you area already registered?")                        
                .build();
                return new_message;
        }
        new Thread(() -> {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpEntity<String> request = new HttpEntity<String>(message_text);
        
                Task taskResponse = restTemplate.postForEntity("http://openai-flask-service:5134/task", request, Task.class).getBody();
                loggerHandler.info(taskResponse.toString());
                taskResponse.setEmployee(employee);
                taskServiceImpl.create(taskResponse);
            }
            catch (Exception e) {
                loggerHandler.error("thread error", e);
            }
        }).start();
        SendMessage new_message = SendMessage
            .builder()
            .chatId(chat_id)
            .text("Adding task... this might take a couple seconds.")
            .build();
        return new_message;
    }
    
    public void handleUpdate(Integer task_id, Integer state){
        Task task = taskServiceImpl.findById(task_id);
        task.setStateTask(state);
        taskServiceImpl.update(task);
    }
    
    public void handleDeleteTask (Integer task_id){
        taskServiceImpl.delete(task_id);

    }

    public EditMessageText handleSendTask(long chat_id, Integer task_id, Integer message_id){
        Task task = taskServiceImpl.findById(task_id);
        String status;
        String textLarge;
        String textFirstHalf;
        String textSecondHalf;
        
        if (task.getStateTask().equals(0)){
            status = EmojiParser.parseToUnicode("ToDo :memo: ");
            textLarge = EmojiParser.parseToUnicode(":hourglass: Inprogress");
            textFirstHalf = EmojiParser.parseToUnicode(":white_check_mark: Complete");
            textSecondHalf = EmojiParser.parseToUnicode(":wastebasket: Delete");
        }
        else if (task.getStateTask().equals(1)){
            status = EmojiParser.parseToUnicode("InProgress :hourglass:");
            textLarge = EmojiParser.parseToUnicode(":white_check_mark: Complete");
            textFirstHalf = EmojiParser.parseToUnicode(":memo: ToDo");
            textSecondHalf = EmojiParser.parseToUnicode(":wastebasket: Delete");
        }
        else {
            status = EmojiParser.parseToUnicode("Completed :white_check_mark:");
            textLarge = EmojiParser.parseToUnicode(":wastebasket: Delete");
            textFirstHalf = EmojiParser.parseToUnicode(":hourglass: Inprogress");
            textSecondHalf = EmojiParser.parseToUnicode(":memo: ToDo");
        }
        
        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow rowBackButton = new InlineKeyboardRow();
        InlineKeyboardRow rowLargeButton = new InlineKeyboardRow();
        InlineKeyboardRow rowHalfsButton = new InlineKeyboardRow();

        InlineKeyboardButton buttonBack = new InlineKeyboardButton("<< Back to "+ status);

        InlineKeyboardButton buttonLarge = new InlineKeyboardButton(textLarge);
        InlineKeyboardButton buttonFirstHalf = new InlineKeyboardButton(textFirstHalf);
        InlineKeyboardButton buttonSecondHalf = new InlineKeyboardButton(textSecondHalf);

        buttonBack.setCallbackData(BotCommands.GET_STATE_TASKS_IMP.getCommand() + " " + task.getStateTask());            
        buttonLarge.setCallbackData(textLarge.split("\\s+")[1] + " " + task_id);
        buttonFirstHalf.setCallbackData(textFirstHalf.split("\\s+")[1] + " " + task_id);
        buttonSecondHalf.setCallbackData(textSecondHalf.split("\\s+")[1] + " " + task_id);
        
        rowBackButton.add(buttonBack);
        rowHalfsButton.add(buttonFirstHalf);
        rowHalfsButton.add(buttonSecondHalf);
        rowLargeButton.add(buttonLarge);

        rows.add(rowBackButton);
        rows.add(rowHalfsButton);
        rows.add(rowLargeButton);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);

        String dueDate = task.getDueDate() != null ? task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : "No due date";
        String startDate = task.getDueDate() != null ? task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : "No start date";
        String text =  "<b>" + "Title: " + task.getTitle() + "</b>" + "\n" + 
        "Description: "+ task.getDescription()+ "\n" + 
        "Status: " + status + "\n" + 
        "Start Date: "  + startDate + "\n" +  
        "Due Date: " + dueDate + "\n";
        
        

        EditMessageText new_message = EditMessageText
            .builder()
            .chatId(chat_id)
            .messageId(message_id)
            .text(text)
            .parseMode("HTML")
            .replyMarkup(inlineKeyboardMarkup)
            .build();

        return new_message;
    }

    public EditMessageText handleGetTodoListByState(long chat_id, Integer message_id, Integer task_state){
        try {
            Employee employee = employeeServiceImpl.findByTelegramId(chat_id);
            List<Task> listByState = taskServiceImpl.toDoStateTasks(employee.getId(), task_state);

            String toDoTaskTitle;
            if (task_state.equals(0)){
                toDoTaskTitle = EmojiParser.parseToUnicode("ToDo tasks: :memo: \n");
            }
            else if (task_state.equals(1)){
                toDoTaskTitle = EmojiParser.parseToUnicode("InProgress tasks: :hourglass: \n");
            }
            else {
                toDoTaskTitle = EmojiParser.parseToUnicode("Completed tasks: :white_check_mark: \n");
            }

            Integer totalTasks = listByState.size();
            Integer counter = 0; 
            String tasks = toDoTaskTitle;
            for (Task task : listByState) {
                String dueDate = task.getDueDate() != null ? task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : "No due date";
                tasks = tasks + "[ " + ++counter + " ] " + task.getTitle() + " | Due: " + dueDate + "\n";  
            }
            
            Integer numRows = totalTasks / 4 + (totalTasks % 4 == 0 ? 0 : 1); // 6 tasks = 2 rows
            Integer tempButtons = totalTasks; 
            Integer numButtons = 0;
            List<InlineKeyboardRow> rows = new ArrayList<>();
            
            counter = 0;

            InlineKeyboardRow rowBackButton = new InlineKeyboardRow();
            InlineKeyboardButton buttonBack = new InlineKeyboardButton("<< Back to All Tasks");
            buttonBack.setCallbackData(BotCommands.TODO_LIST_COMMAND.getCommand());
            rowBackButton.add(buttonBack);
            rows.add(rowBackButton);

            for(int i = 0; i < numRows; i++){
                InlineKeyboardRow row = new InlineKeyboardRow();

                if(tempButtons > 4){
                    numButtons = 4;
                    tempButtons = tempButtons - 4;
                }
                else{
                    numButtons = tempButtons;
                }

                for(int j = 0; j < numButtons; j++){
                    InlineKeyboardButton button = new InlineKeyboardButton(String.format("%d", ++counter));
                    button.setCallbackData(BotCommands.GET_TASK_COMMAND.getCommand() + " " + listByState.get(counter-1).getId());
                    row.add(button);
                }
                rows.add(row);
            }
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                
            EditMessageText new_message = EditMessageText
            .builder()
            .chatId(chat_id)
            .messageId(message_id)
            .text(tasks)
            .replyMarkup(inlineKeyboardMarkup)
            .build();

            return new_message;
        }
        catch (EntityNotFoundException e) {
            loggerHandler.error("Entity not found", e);
            EditMessageText new_message = EditMessageText
                    .builder()
                    .chatId(chat_id)
                    .messageId(message_id)
                    .text("I did not find you on our internal database, are you sure you area already registered?")
                    .build();
            return new_message;
        }
        catch (Exception e){
            loggerHandler.error("General error", e);
            EditMessageText new_message = EditMessageText
                    .builder()
                    .chatId(chat_id)
                    .messageId(message_id)
                    .text("500: Internal Server Error, sorry :(")
                    .build();
            return new_message;
        }
    }
    
    public Object getTodoList(long chat_id, Integer messageId) {
        try {
            Employee modifyEmployee = employeeServiceImpl.findByTelegramId(chat_id);
            List<Task> toDoList = taskServiceImpl.allEmployeeTasks(modifyEmployee.getId());
            
            if(toDoList.size() != 0){
                String toDoTask = EmojiParser.parseToUnicode("ToDo tasks: :memo: \n");
                String inProgressTask = EmojiParser.parseToUnicode("InProgress tasks: :hourglass: \n");
                String completedTask = EmojiParser.parseToUnicode("Completed tasks: :white_check_mark: \n");
        
                for (Task task : toDoList) {
                    String dueDate = task.getDueDate() != null ? task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() : "No due date";

                    if (task.getStateTask().equals(0)){
                        toDoTask = toDoTask + "- " + task.getTitle() + " | Due: " + dueDate + "\n";
                    } else if (task.getStateTask().equals(1)){
                        inProgressTask = inProgressTask + "- " + task.getTitle() + " | Due: " + dueDate + "\n";
                    } else if (task.getStateTask().equals(2)){
                        completedTask = completedTask + "- " + task.getTitle() + " | Due: " + dueDate + "\n";
                    }
                }
        
                List<InlineKeyboardRow> rows = new ArrayList<>();
        
                InlineKeyboardRow row = new InlineKeyboardRow();
                InlineKeyboardRow row2 = new InlineKeyboardRow();
        
                InlineKeyboardButton buttonToDoTask = new InlineKeyboardButton(EmojiParser.parseToUnicode(":memo: ToDo Tasks"));
                InlineKeyboardButton buttonInProgress = new InlineKeyboardButton(EmojiParser.parseToUnicode(":hourglass: InProgress"));
                InlineKeyboardButton buttonCompleted = new InlineKeyboardButton(EmojiParser.parseToUnicode(":white_check_mark: Completed"));
                buttonToDoTask.setCallbackData("GET_STATE_TASKS 0");
                buttonInProgress.setCallbackData("GET_STATE_TASKS 1");
                buttonCompleted.setCallbackData("GET_STATE_TASKS 2");
        
                row.add(buttonToDoTask);
                row2.add(buttonInProgress);
                row2.add(buttonCompleted);
        
                rows.add(row);
                rows.add(row2);
        
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        
                if (messageId == null) {
                    return SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text(toDoTask + inProgressTask + completedTask)
                            .replyMarkup(inlineKeyboardMarkup)
                            .build();
                } else { 
                    return EditMessageText
                            .builder()
                            .chatId(chat_id)
                            .messageId(messageId)
                            .text(toDoTask + inProgressTask + completedTask)
                            .replyMarkup(inlineKeyboardMarkup)
                            .build();
                }
            } else {
                List<InlineKeyboardRow> rows = new ArrayList<>();
                InlineKeyboardRow row = new InlineKeyboardRow();
                InlineKeyboardButton buttonToDoTask = new InlineKeyboardButton(EmojiParser.parseToUnicode(":memo: Add ToDo Tasks"));
                buttonToDoTask.setCallbackData("/addtask");
                row.add(buttonToDoTask);
                rows.add(row);
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);


                return EditMessageText
                            .builder()
                            .chatId(chat_id)
                            .messageId(messageId)
                            .text("Your task list is empty!")
                            .replyMarkup(inlineKeyboardMarkup)
                            .build();
            }
            
        } catch (EntityNotFoundException e) {
            loggerHandler.error("Entity not found", e);
        } catch (Exception e) {
            loggerHandler.error("General error", e);
        }
        return null;
    }
}

