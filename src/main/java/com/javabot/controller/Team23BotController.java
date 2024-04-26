package com.javabot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


@SuppressWarnings("deprecation")
@Controller
@RequestMapping(path="/bot")
public class Team23BotController extends TelegramWebhookBot  {

    private String botName = System.getenv("BOT_CREDENTIALS_USR");
    private String botPath = "https://api.romongo.uk/bot/" + System.getenv("BOT_CREDENTIALS_PWD");    
    @Override
	public String getBotUsername() {		
		return botName;
	}

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    @PostMapping(path="/extreme-bot-endpoint")
    public BotApiMethod<?> onWebhookUpdateReceived(@RequestBody Update update) {
        
        
        String messageTextFromTelegram = update.getMessage().getText();
        System.out.println(messageTextFromTelegram);
        long chatId = update.getMessage().getChatId();
        System.out.println(chatId);
        long userId = update.getMessage().getFrom().getId();
        System.out.println(userId);
        messageTextFromTelegram = messageTextFromTelegram + " " + userId;
        

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Hello world");
        
        //String urlToSendMessage = "https://api.telegram.org/bot"+System.getenv("BOT_CREDENTIALS_PWD")+"/sendMessage?chat_id=" + chatId + "&text=" + messageTextFromTelegram;

        return sendMessage;    
        /* 
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            logger.error(e.getLocalizedMessage(), e);      //Any error will be printed here
        }
        */
    }


}
