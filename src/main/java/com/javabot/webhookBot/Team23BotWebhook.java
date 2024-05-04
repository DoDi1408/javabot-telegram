package com.javabot.webhookBot;


import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.generics.TelegramClient;
//import org.telegram.telegrambots.webhook.starter.SpringTelegramWebhookBot;
public class Team23BotWebhook /* extends SpringTelegramWebhookBot*/{

    private final TelegramClient telegramClient;


    public Team23BotWebhook(String botToken){
        //super("https://api.romongo.uk/extreme-bot-endpoint", new UpdateHandler(), new setWebhookRunnable(), new deleteWebhookRunnable());
        telegramClient = new OkHttpTelegramClient(botToken);
        SendMessage message = new SendMessage("1111111", "botToken");
        try {
            telegramClient.execute(message);
        } catch (Exception e) {
            System.out.println("troll");
        }
    }
    public String getBotPath(){
        return "/extreme-bot-endpoint";
    }
}