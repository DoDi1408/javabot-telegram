package com.javabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.webhook.starter.TelegramBotsSpringWebhookApplication;

import com.javabot.webhookBot.Team23BotWebhook;;


@SpringBootApplication
public class TelegramBotTeam23Application{
	
	public static void main(String[] args) {
		try {
		TelegramBotsSpringWebhookApplication tapp = new TelegramBotsSpringWebhookApplication();
		tapp.registerBot(new Team23BotWebhook());
		} catch (TelegramApiException e){
			e.printStackTrace();
		}
		SpringApplication.run(TelegramBotTeam23Application.class, args);
	}

}
