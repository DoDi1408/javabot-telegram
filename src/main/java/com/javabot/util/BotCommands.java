package com.javabot.util;

public enum BotCommands {

	START_COMMAND("/start"), 
	REGISTER_EMP_COMMAND("/registeremployee"), 
	REGISTER_MAN_COMMAND("/registermanager"),
	REGISTER_MANAGER("REGISTER_MANAGER"),
	JOIN_TEAM("/jointeam"),
	JOIN_TEAM_IMP("JOIN_TEAM"),
	TODO_LIST("/todolist"),
	TEAM_LIST("/teamlist"),
	ADD_ITEM("/addtask"),
	ADD_TASK("ADD_TASK"),
	UPDATE_TASK("/updatetask"),
	ADVANCE_TASK("PROCEED_TASK"),
	REVERT_TASK("REVERT_TASK"),
	DELETE_COMMAND("/deletetask"),
	DELETE_TASK("DELETE_TASK");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}