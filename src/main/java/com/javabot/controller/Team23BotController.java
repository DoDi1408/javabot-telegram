package com.javabot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.javabot.Team23Bot;


@Controller
@RequestMapping(path="/bot")
public class Team23BotController{

    private final static Logger loggerController = LoggerFactory.getLogger(Team23BotController.class);
    private Team23Bot Team23BotInstance = new Team23Bot();

    @PostMapping(path="/extreme-bot-endpoint")
    public BotApiMethod<?> onWebhookReceived(@RequestBody Update update) {
        loggerController.info("Message received to controller");
        return Team23BotInstance.onWebhookUpdateReceived(update);
    }
    


}
