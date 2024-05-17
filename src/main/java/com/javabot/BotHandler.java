package com.javabot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;



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
                        employeeServiceImpl.create(modifyEmployee);
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

    public SendMessage getTodoListTeam(long chat_id){
        try {
            Employee modifyEmployee = employeeServiceImpl.findByTelegramId(chat_id);
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
                List <Task> teamTasks = taskServiceImpl.allTeamTasks(teamID);

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
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(addItem)
                    .build();
        return new_message;
    }

    public SendMessage addTask(long chat_id, String message_text, String[] words){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(words[1]);
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
            int firstSpace = message_text.indexOf(' ');
            int secondSpace = message_text.indexOf(' ', firstSpace + 1);

            String title = message_text.substring(secondSpace + 1);
            Date currentTime = new Date();
            Task newTask = new Task(currentTime,date,title,"",employee);
            try {
                taskServiceImpl.create(newTask);
                SendMessage new_message = SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text("Successfully added a new task!")
                            .build();
                return new_message;
            }
            catch (Exception e){
                loggerHandler.error("Some unknown error occurred",e);
                SendMessage new_message = SendMessage
                            .builder()
                            .chatId(chat_id)
                            .text("Internal server error")
                            .build();
                return new_message;
            }
        }
        catch (ParseException e){
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Incorrect date format")
                    .build();
            return new_message;
        }
    }
    
    public SendMessage handleUpdateTaskCommand(long chat_id){
        String update = "To update a task either type PROCEED_TASK or REVERT_TASK and a valid task ID (You can check those through the /todoList command). \n A Task's Lifecycle looks like this: \n Todo -> InProgress -> Completed";
        SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(update)
                    .build();
        return new_message;
    }

    public void handleUpdate(Integer task_id, Integer state){
        Task task = taskServiceImpl.findById(task_id);
        task.setStateTask(state);
        taskServiceImpl.update(task);
    }

    public SendMessage handleUpdateTask(long chat_id, Integer task_id, boolean AdvanceOrGoBack){
        Task task = taskServiceImpl.findById(task_id);
        Integer stateTask = task.getStateTask();
        long telegramIdOwner = task.getEmployee().getTelegramId();
        if (telegramIdOwner == chat_id){
            if (AdvanceOrGoBack){
                if (stateTask == 2){
                    SendMessage new_message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Completed is the highest stage in a task's lifecycle.")
                        .build();
                    return new_message;
                }
                else{
                    task.setStateTask(stateTask+1);
                    taskServiceImpl.update(task);
                    SendMessage new_message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Advanced task with id " + task.getId())
                        .build();
                    return new_message;
                }
            }
            else {
                if (stateTask == 0){
                    SendMessage new_message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("ToDo is the lowest stage in a task's lifecycle.")
                        .build();
                    return new_message;
                }
                else{
                    task.setStateTask(stateTask-1);
                    taskServiceImpl.update(task);
                    SendMessage new_message = SendMessage
                        .builder()
                        .chatId(chat_id)
                        .text("Reverted task with id " + task.getId())
                        .build();
                    return new_message;
                }
            }
        }
        else {
            SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("You cant modify a task you don't own.")
                    .build();
            return new_message;
        }
    }
    
    public void handleDeleteTask (Integer task_id){
        taskServiceImpl.findById(task_id);
    }

    public EditMessageText handleSendTask(long chat_id, Integer task_id, Integer message_id){
        Task task = taskServiceImpl.findById(task_id);
        String status;
        String textLarge;
        String textFirstHalf;
        String textSecondHalf;
        
        if (task.getStateTask().equals(0)){
            status = EmojiParser.parseToUnicode("ToDo :memo: ");
            textLarge = EmojiParser.parseToUnicode("Inprogress");
            textFirstHalf = EmojiParser.parseToUnicode("Complete");
            textSecondHalf = EmojiParser.parseToUnicode("Delete");
        }
        else if (task.getStateTask().equals(1)){
            status = EmojiParser.parseToUnicode("InProgress :hourglass: ");
            textLarge = EmojiParser.parseToUnicode("Complete");
            textFirstHalf = EmojiParser.parseToUnicode("ToDo");
            textSecondHalf = EmojiParser.parseToUnicode("Delete");
        }
        else {
            status = EmojiParser.parseToUnicode("Completed :white_check_mark: ");
            textLarge = EmojiParser.parseToUnicode("Delete");
            textFirstHalf = EmojiParser.parseToUnicode("Inprogress");
            textSecondHalf = EmojiParser.parseToUnicode("ToDo");
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
        buttonLarge.setCallbackData(textLarge + " " + task_id);
        buttonFirstHalf.setCallbackData(textFirstHalf + " " + task_id);
        buttonSecondHalf.setCallbackData(textSecondHalf + " " + task_id);
        
        rowBackButton.add(buttonBack);
        rowHalfsButton.add(buttonFirstHalf);
        rowHalfsButton.add(buttonSecondHalf);
        rowLargeButton.add(buttonLarge);

        rows.add(rowBackButton);
        rows.add(rowHalfsButton);
        rows.add(rowLargeButton);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        
        String text =  "<b>" + "Title: " + task.getTitle() + "</b>" + "\n" + 
        "Description: "+ task.getDescription()+ "\n" + 
        "Status: " + status + "\n" + 
        "Start Date: "  + task.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n" +  
        "Due Date: " + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
        
        

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
                    tasks = tasks + "[ " + ++counter + " ] " + task.getTitle() + " | Due: " + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";  
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
    
    public Object getTodoList(long chatId, Integer messageId) {
        try {
            Employee modifyEmployee = employeeServiceImpl.findByTelegramId(chatId);
            List<Task> toDoList = taskServiceImpl.allEmployeeTasks(modifyEmployee.getId());
            
            if(toDoList.size() != 0){
                String toDoTask = EmojiParser.parseToUnicode("ToDo tasks: :memo: \n");
                String inProgressTask = EmojiParser.parseToUnicode("InProgress tasks: :hourglass: \n");
                String completedTask = EmojiParser.parseToUnicode("Completed tasks: :white_check_mark: \n");
        
                for (Task task : toDoList) {
                    if (task.getStateTask().equals(0)){
                        toDoTask = toDoTask + "- " + task.getTitle() + " | Due: " + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                    } else if (task.getStateTask().equals(1)){
                        inProgressTask = inProgressTask + "- " + task.getTitle() + " | Due: " + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
                    } else if (task.getStateTask().equals(2)){
                        completedTask = completedTask + "- " + task.getTitle() + " | Due: " + task.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() + "\n";
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
                            .chatId(chatId)
                            .text(toDoTask + inProgressTask + completedTask)
                            .replyMarkup(inlineKeyboardMarkup)
                            .build();
                } else { 
                    return EditMessageText
                            .builder()
                            .chatId(chatId)
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
                            .chatId(chatId)
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

