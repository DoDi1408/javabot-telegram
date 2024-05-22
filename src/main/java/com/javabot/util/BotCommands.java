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
	GET_STATE_TASKS_IMP("GET_STATE_TASKS"),
	GET_TASK_COMMAND("getTask"),
	GET_EMPLOYEE_TASK("GET_EMPLOYEE_TASK"),

	REGISTER_MANAGER_IMP("REGISTER_MANAGER"),
	JOIN_TEAM_IMP("JOIN_TEAM"),
	ADD_TASK_IMP("ADD_TASK"),
	
	DELETE_TASK("Delete"),
	TODO_TASK("ToDo"),
	INPROGRESS_TASK("Inprogress"),
	COMPLETE_TASK("Complete");

	private String command;

	BotCommands(String enumCommand) {
		this.command = enumCommand;
	}

	public String getCommand() {
		return command;
	}
}