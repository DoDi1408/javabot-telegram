package com.javabot.util;

public enum BotCommands {

	START_COMMAND("/start"), 
	REGISTER_EMP_COMMAND("/registerEmployee"), 
	REGISTER_MAN_COMMAND("/registerManager"),
	REGISTER_MANAGER("REGISTER_MANAGER"),
	JOIN_TEAM("/joinTeam"),
	JOIN_TEAM_IMP("JOIN_TEAM"),
	TODO_LIST("/todoList"),
	TEAM_LIST("/teamList"),
	ADD_ITEM("/additem"),
	ADD_TASK("ADD_TASK"),
	UPDATE_TASK("/updateTask"),
	ADVANCE_TASK("PROCEED_TASK"),
	REVERT_TASK("REVERT_TASK");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}