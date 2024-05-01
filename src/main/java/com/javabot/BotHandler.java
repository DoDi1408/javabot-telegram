package com.javabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

import com.javabot.models.Employee;
import com.javabot.service.EmployeeRepository;
import com.javabot.service.ManagerServiceImpl;

@Component
public class BotHandler {

    private final static Logger loggerHandler = LoggerFactory.getLogger(BotHandler.class);

    @Autowired
    EmployeeRepository EmployeeRepository;

    @Autowired
    ManagerServiceImpl managerServiceImpl;

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
}
