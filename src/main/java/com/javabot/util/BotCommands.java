package com.javabot.util;

public enum BotCommands {

	START_COMMAND("/start"),
	REGISTER_COMMAND("/register"), 
	REGISTER_EMP_COMMAND("/registeremployee"), 
	REGISTER_MAN_COMMAND("/registermanager"),
	JOIN_TEAM_COMMAND("/jointeam"),
	TODO_LIST_COMMAND("/todolist"),
	TEAM_LIST_COMMAND("/teamlist"),
	ADD_TASK_COMMAND("/addtask"),
	UPDATE_TASK_COMMAND("/updatetask"),
	DELETE_COMMAND("/deletetask"),
	GET_STATE_TASKS_IMP("GET_STATE_TASKS"),
	GET_TASK_COMMAND("getTask"),

	REGISTER_MANAGER_IMP("REGISTER_MANAGER"),
	JOIN_TEAM_IMP("JOIN_TEAM"),
	ADD_TASK_IMP("ADD_TASK"),
	PROCEED_TASK_IMP("PROCEED_TASK"),
	REVERT_TASK_IMP("REVERT_TASK"),
	DELETE_TASK_IMP("DELETE_TASK");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}