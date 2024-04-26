package com.javabot.telegram;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@SuppressWarnings("deprecation")
@Controller
@RequestMapping(path="/bot")
public class Team23BotController extends TelegramWebhookBot  {

    private String botName = System.getenv("BOT_CREDENTIALS_USR");
    private String botPath = "/bot/" + System.getenv("BOT_CREDENTIALS_PWD");    
    @Override
	public String getBotUsername() {		
		return botName;
	}

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    @PostMapping(
            value = "/{botPath}"
    )
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        String messageTextFromTelegram = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        messageTextFromTelegram = messageTextFromTelegram + " " + userId;
        
        SendMessage sm  = SendMessage.builder().chatId(chatId).text(messageTextFromTelegram).build();

        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
        return null;
    }


}
