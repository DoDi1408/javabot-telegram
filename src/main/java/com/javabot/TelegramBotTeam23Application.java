package com.javabot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.javabot.controller.Team23BotController;



@SpringBootApplication
public class TelegramBotTeam23Application implements CommandLineRunner{
	
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotTeam23Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			String token = System.getenv("BOT_CREDENTIALS_PSW");
			String name = System.getenv("BOT_CREDENTIALS_USR");
			System.out.println(token);
			System.out.println(name);
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new Team23BotController(token, name));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
