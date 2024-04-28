package com.javabot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Controller
@RequestMapping(path="/bot")
public class Team23BotController extends TelegramWebhookBot  {

    private final static Logger logger = LoggerFactory.getLogger(Team23BotController.class);
    private String botName = System.getenv("BOT_CREDENTIALS_USR");
    private String botPath = "https://api.romongo.uk/bot/extreme-bot-endpoint";
    private String botToken = System.getenv("BOT_CREDENTIALS_PSW");

    @SuppressWarnings("deprecation")
    public Team23BotController() {
        logger.info("Bot Token: " + botToken);
		logger.info("Bot name: " + botName);
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

    
    @Override
    @PostMapping(path="/extreme-bot-endpoint")
    public BotApiMethod<?> onWebhookUpdateReceived(@RequestBody Update update) {
        
        
        String messageTextFromTelegram = update.getMessage().getText();
        long userId = update.getMessage().getFrom().getId();
        messageTextFromTelegram = messageTextFromTelegram + " " + userId;
        

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(userId));
        sendMessage.setText(messageTextFromTelegram);
        
        //String urlToSendMessage = "https://api.telegram.org/bot"+System.getenv("BOT_CREDENTIALS_PWD")+"/sendMessage?chat_id=" + chatId + "&text=" + messageTextFromTelegram;
        try {
            execute(sendMessage);
        } catch (TelegramApiException e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }
    


}
