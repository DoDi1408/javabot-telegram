package com.javabot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Team23Bot extends TelegramWebhookBot {
    private final static Logger loggerBot = LoggerFactory.getLogger(Team23Bot.class);
    private String botName;
    private String botPath;
    private String botToken;

    public Team23Bot() {
        super(System.getenv("BOT_CREDENTIALS_PSW"));
        this.botToken = (System.getenv("BOT_CREDENTIALS_PSW"));
        this.botPath = "https://api.romongo.uk/bot/extreme-bot-endpoint";
        this.botName = System.getenv("BOT_CREDENTIALS_USR");
        loggerBot.info("Bot Token: " + botToken);
		loggerBot.info("Bot name: " + botName);
	}

    @Override
	public String getBotUsername() {		
		return botName;
	}
    @Override
    public String getBotToken(){
        return botToken;
    }
    
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

    /*
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
