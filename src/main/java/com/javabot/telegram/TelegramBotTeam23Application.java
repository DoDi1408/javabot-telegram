package com.javabot.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class TelegramBotTeam23Application {
	
	public static void main(String[] args) {
		SpringApplication.run(TelegramBotTeam23Application.class, args);
	}
	/*
	private String botName = System.getenv("BOT_CREDENTIALS_USR");
	private String botPath = System.getenv("BOT_CREDENTIALS_PWD");
	
	@Override
	public void run(String... args) throws Exception {
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			
			telegramBotsApi.registerBot(new Team23BotController(botName,botPath),new SetWebhook("https://api.romongo.uk/" + botPath));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	*/

}
