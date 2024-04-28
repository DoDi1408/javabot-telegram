package com.javabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.bots.TelegramWebhookBot;
//import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class Team23BotLongPolling implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer{
    private final TelegramClient telegramClient;

    private final static Logger loggerBot = LoggerFactory.getLogger(Team23BotLongPolling.class);

    public Team23BotLongPolling() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        /*
        
        super(System.getenv("BOT_CREDENTIALS_PSW"));
        this.botToken = (System.getenv("BOT_CREDENTIALS_PSW"));
        this.botPath = "https://api.romongo.uk/bot/extreme-bot-endpoint";
        this.botName = System.getenv("BOT_CREDENTIALS_USR");
        loggerBot.info("Bot Token: " + botToken);
		loggerBot.info("Bot name: " + botName);

        */
	}
    /*
    @Override
	public String getBotUsername() {		
		return botName;
	}
    */

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
            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(message_text)
                    .build();
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }

    /*
    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        
        
        String messageTextFromTelegram = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        loggerBot.info("Recieved message from " + userId + ", with text content " + messageTextFromTelegram);
        messageTextFromTelegram = messageTextFromTelegram + " " + userId;
        

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(userId));
        sendMessage.setText(messageTextFromTelegram);
        
        //String urlToSendMessage = "https://api.telegram.org/bot"+System.getenv("BOT_CREDENTIALS_PWD")+"/sendMessage?chat_id=" + chatId + "&text=" + messageTextFromTelegram;
        try {
            execute(sendMessage);
        } catch (TelegramApiException e){
            loggerBot.error(e.getLocalizedMessage(),e);
        }

        try {
            sendMessage.validate();
        } catch (TelegramApiException e){
            loggerBot.error(e.getLocalizedMessage(),e);
        }
        return sendMessage;
    }

    
    @Override
    public void onUpdateReceived(Update update){
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageTextFromTelegram = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(messageTextFromTelegram);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                logger.error(e.getLocalizedMessage(), e);
            }

        }
    }
    */


}
