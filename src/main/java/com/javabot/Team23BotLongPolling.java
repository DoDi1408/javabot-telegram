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

            else if (message_text.equals(BotCommands.REGISTER_COMMAND.getCommand())){
                SendMessage message = handler.handleRegistration(update);
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    loggerBot.error("API Exception",e);
                }
            }

            else if (message_text.equals(BotCommands.TODO_LIST.getCommand())){
                SendMessage message = handler.handleRegistration(update);
                try {
                    telegramClient.execute(message);
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
