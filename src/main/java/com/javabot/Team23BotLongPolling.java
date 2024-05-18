package com.javabot;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.javabot.util.BotCommands;

@Component
public class Team23BotLongPolling  implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    
    @Autowired
    private BotHandler handler;

    private final static Logger loggerBot = LoggerFactory.getLogger(Team23BotLongPolling.class);
    
    private Map<Long , String> userStatesMap = new HashMap<Long, String>();

    public Team23BotLongPolling() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public String getBotToken(){
        return System.getenv("BOT_CREDENTIALS_PSW");
    }

    public void executeTelegramAction(SendMessage message, EditMessageText editedMessage) {
        try {
            if (message != null) {
                telegramClient.execute(message);
            }
            if (editedMessage != null) {
                telegramClient.execute(editedMessage);
            }
        } catch (TelegramApiException e) {
            loggerBot.error("API Exception", e);
        }
    }
    
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();

            String[] words = message_text.split(" ");

            long chat_id = update.getMessage().getChatId();
            loggerBot.info("Recieved message from " + chat_id + ", with text content " + message_text);

            if (message_text.equals(BotCommands.START_COMMAND.getCommand())){
                SendMessage message = handler.handleStart(chat_id);
                executeTelegramAction(message, null);
            }

            else if (message_text.equals(BotCommands.REGISTER_COMMAND.getCommand())) {                
                SendMessage message = handler.handleRegistration(chat_id);
                executeTelegramAction(message, null);
            }
            
            else if (message_text.equals(BotCommands.JOIN_TEAM_COMMAND.getCommand())){
                SendMessage message = handler.handleGetTeams(chat_id);
                executeTelegramAction(message, null);
            }

            else if (message_text.equals(BotCommands.TODO_LIST_COMMAND.getCommand())){
                SendMessage message = (SendMessage) handler.getTodoList(chat_id, null);
                executeTelegramAction(message, null);
            }

            else if (message_text.equals(BotCommands.TEAM_LIST_COMMAND.getCommand())){
                loggerBot.info(message_text);
                SendMessage message = (SendMessage) handler.getTodoListTeam(chat_id, null);
                executeTelegramAction(message, null);
            }

            else if (message_text.equals(BotCommands.ADD_TASK_COMMAND.getCommand())){
                SendMessage message = handler.handleAddItem(chat_id);
                executeTelegramAction(message, null);
            }
            
            else if (words[0].equals(BotCommands.ADD_TASK_IMP.getCommand())){
                SendMessage message = handler.addTask(chat_id, message_text, words);
                executeTelegramAction(message, null);
            }            
            else{
                if (userStatesMap.containsKey(chat_id)){
                    String userState = userStatesMap.get(chat_id);
                    String[] userStateSplit = userState.split(" ");
                    if (userStateSplit[0] == "REGISTER_MANAGER"){
                        SendMessage message = handler.handleRegistrationManagerReal(chat_id,update.getMessage().getFrom(),message_text, userStatesMap);
                        executeTelegramAction(message, null);
                    }
                }
                else{
                    SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("I don't know how to react to that :(")
                    .build();
                    executeTelegramAction(message, null);
                }  
            }
        }

        else if(update.hasCallbackQuery()){
            
            String callback_data = update.getCallbackQuery().getData();
            Long chat_id = update.getCallbackQuery().getMessage().getChatId();
            Integer message_id = update.getCallbackQuery().getMessage().getMessageId();
            String[] words = callback_data.split(" ");
            
            loggerBot.info(callback_data);

            if (callback_data.equals(BotCommands.REGISTER_EMP_COMMAND.getCommand())){
                SendMessage message = handler.handleRegistrationEmployee(chat_id, update.getCallbackQuery().getFrom());
                EditMessageText edited_message = EditMessageText
                .builder()
                .chatId(chat_id)
                .messageId(message_id)
                .text("You have choosen Employe Registration!")
                .build();
                executeTelegramAction(message, edited_message);
            }

            else if (callback_data.equals(BotCommands.REGISTER_MAN_COMMAND.getCommand())){
                SendMessage message = handler.handleRegistrationManager(chat_id, userStatesMap);
                EditMessageText edited_message = EditMessageText
                .builder()
                .chatId(chat_id)
                .messageId(message_id)
                .text("You have choosen Manager Registration!")
                .build();
                executeTelegramAction(message, edited_message);
            }

            else if (words[0].equals(BotCommands.JOIN_TEAM_IMP.getCommand())){
                SendMessage message = handler.handleChangeTeam(chat_id,words[1]);
                executeTelegramAction(message, null);
            }

            else if (words[0].equals(BotCommands.GET_TASK_COMMAND.getCommand())){
                EditMessageText edited_message = handler.handleSendTask(chat_id,Integer.valueOf(words[1]), message_id);
                executeTelegramAction(null, edited_message);
            }

            else if (callback_data.equals(BotCommands.TEAM_LIST_COMMAND.getCommand())){
                loggerBot.info(callback_data);
                EditMessageText edited_message = (EditMessageText) handler.getTodoListTeam(chat_id, message_id);
                executeTelegramAction(null, edited_message);
            }

            else if (words[0].equals(BotCommands.GET_EMPLOYEE_TASK.getCommand())){
                EditMessageText edited_message = handler.handleSendEmployeeTask(chat_id,Integer.valueOf(words[1]), message_id);
                executeTelegramAction(null, edited_message);
            }

            else if (words[0].equals(BotCommands.GET_STATE_TASKS_IMP.getCommand())){
                Integer task_state = Integer.parseInt(words[1]);
                EditMessageText edited_message = handler.handleGetTodoListByState(chat_id, message_id, task_state);
                executeTelegramAction(null, edited_message);
            }
            
            else if (callback_data.equals(BotCommands.TODO_LIST_COMMAND.getCommand())){
                EditMessageText edited_message = (EditMessageText) handler.getTodoList(chat_id, message_id);
                executeTelegramAction(null, edited_message);
            }
            else if(words[0].equals(BotCommands.DELETE_TASK.getCommand())){
                handler.handleDeleteTask(Integer.parseInt(words[1]));
                EditMessageText edited_message = (EditMessageText) handler.getTodoList(chat_id, message_id);
                executeTelegramAction(null, edited_message);
            }
            else if(words[0].equals(BotCommands.COMPLETE_TASK.getCommand())){
                handler.handleUpdate(Integer.parseInt(words[1]), 2);
                EditMessageText edited_message = (EditMessageText) handler.getTodoList(chat_id, message_id);
                executeTelegramAction(null, edited_message);
            }
            else if(words[0].equals(BotCommands.INPROGRESS_TASK.getCommand())){
                handler.handleUpdate(Integer.parseInt(words[1]), 1);
                EditMessageText edited_message = (EditMessageText) handler.getTodoList(chat_id, message_id);
                executeTelegramAction(null, edited_message);
            }
            else if(words[0].equals(BotCommands.TODO_TASK.getCommand())){
                handler.handleUpdate(Integer.parseInt(words[1]), 0);
                EditMessageText edited_message = (EditMessageText) handler.getTodoList(chat_id, message_id);
                executeTelegramAction(null, edited_message);
            }
        }        
    }
    
    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        loggerBot.info("Registered bot running state is: " + botSession.isRunning());
    }

}
