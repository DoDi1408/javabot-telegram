package com.javabot.util;

public enum BotCommands {

	START_COMMAND("/start"), 
	REGISTER_COMMAND("/register"), 
	TODO_LIST("/todolist"),
	ADD_ITEM("/additem");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}