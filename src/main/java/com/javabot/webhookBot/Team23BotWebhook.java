package com.javabot.webhookBot;


//import org.telegram.telegrambots.webhook.starter.SpringTelegramWebhookBot;

public class Team23BotWebhook /* extends SpringTelegramWebhookBot */{

    public Team23BotWebhook(){
        //super("https://api.romongo.uk/extreme-bot-endpoint", new UpdateHandler(), new setWebhookRunnable(), new deleteWebhookRunnable());
    }
    public String getBotPath(){
        return "https://api.romongo.uk/extreme-bot-endpoint";
    }
}
