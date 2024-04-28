package com.javabot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;



@SpringBootApplication
public class TelegramBotTeam23Application implements CommandLineRunner{
	
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotTeam23Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new Team23Bot(), new SetWebhook("https://api.romongo.uk/bot/extreme-bot-endpoint"));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
