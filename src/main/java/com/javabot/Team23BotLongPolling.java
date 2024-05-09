package com.javabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

// DejaVuSansM Nerd Font
// MesloLGS NF
@Component
public class Team23BotLongPolling  implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    
    @Autowired
    private BotHandler handler;

    private final static Logger loggerBot = LoggerFactory.getLogger(Team23BotLongPolling.class);
    
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
    

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText();

            String[] words = message_text.split(" ");

            long chat_id = update.getMessage().getChatId();
            loggerBot.info("Recieved message from " + chat_id + ", with text content " + message_text);

            if (message_text.equals(BotCommands.START_COMMAND.getCommand())){
                SendMessage message = handler.handleStart(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }

            else if (message_text.equals(BotCommands.REGISTER_EMP_COMMAND.getCommand())){
                SendMessage message = handler.handleRegistrationEmployee(chat_id, update.getMessage().getFrom());
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }

            else if (message_text.equals(BotCommands.REGISTER_MAN_COMMAND.getCommand())){
                SendMessage message = handler.handleRegistrationManager(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }
            else if (message_text.equals(BotCommands.JOIN_TEAM.getCommand())){
                SendMessage message = handler.handleGetTeams(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }

            else if (message_text.equals(BotCommands.TODO_LIST.getCommand())){
                SendMessage message = handler.getTodoList(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }
            else if (message_text.equals(BotCommands.TEAM_LIST.getCommand())){
                SendMessage message = handler.getTodoListTeam(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }
            else if (message_text.equals(BotCommands.ADD_ITEM.getCommand())){
                SendMessage message = handler.handleAddItem(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }
            else if (message_text.equals(BotCommands.UPDATE_TASK.getCommand())){
                SendMessage message = handler.handleUpdateTaskCommand(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }
            else if (message_text.equals(BotCommands.DELETE_COMMAND.getCommand())){
                SendMessage message = handler.handleDeleteCommand(chat_id);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }
            else if (words[0].equals(BotCommands.DELETE_TASK.getCommand())){
                try {
                    int task_id = Integer.parseInt(words[1]);
                    SendMessage message = handler.handleDeleteTask(chat_id, task_id);
                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException e) {
                        loggerBot.error("API Exception",e);
                    }
                } catch (NumberFormatException e) {
                    loggerBot.error("Invalid Task Id (contains non-numeric)", e);
                    SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Invalid Task Id (contains non-numeric character)")
                    .build();
                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException error) {
                        loggerBot.error("API Exception",error);
                    }
                }
            }

            else if (words[0].equals(BotCommands.ADVANCE_TASK.getCommand())){
                try {
                    int task_id = Integer.parseInt(words[1]);
                    SendMessage message = handler.handleUpdateTask(chat_id, task_id, true);
                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException e) {
                        loggerBot.error("API Exception",e);
                    }
                } catch (NumberFormatException e) {
                    loggerBot.error("Invalid Task Id (contains non-numeric)", e);
                    SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Invalid Task Id (contains non-numeric character)")
                    .build();
                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException error) {
                        loggerBot.error("API Exception",error);
                    }
                }
            }
            else if (words[0].equals(BotCommands.REVERT_TASK.getCommand())){
                try {
                    int task_id = Integer.parseInt(words[1]);
                    SendMessage message = handler.handleUpdateTask(chat_id, task_id, false);
                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException e) {
                        loggerBot.error("API Exception",e);
                    }
                } catch (NumberFormatException e) {
                    loggerBot.error("Invalid Task Id (contains non-numeric)", e);
                    SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("Invalid Task Id (contains non-numeric character)")
                    .build();
                    try {
                        telegramClient.execute(message);
                    } catch (TelegramApiException error) {
                        loggerBot.error("API Exception",error);
                    }
                }
            }

            else if (words[0].equals(BotCommands.ADD_TASK.getCommand())){
                SendMessage message = handler.addTask(chat_id, message_text, words);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }

            else if (words[0].equals(BotCommands.REGISTER_MANAGER.getCommand())){
                String teamName = words[1];
                SendMessage message = handler.handleRegistrationManagerReal(chat_id, update.getMessage().getFrom(), teamName);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }
            else if (words[0].equals(BotCommands.JOIN_TEAM_IMP.getCommand())){
                String teamNum = words[1];
                SendMessage message = handler.handleChangeTeam(chat_id, teamNum);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }

            else{
                SendMessage new_message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text("I don't know how to react to that :(")
                    .build();
                    try {
                        telegramClient.execute(new_message);
                    } catch (TelegramApiException e) {
                        loggerBot.error("API Exception",e);
                    }
            }
            
        }
    }
    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        loggerBot.info("Registered bot running state is: " + botSession.isRunning());
    }



}
