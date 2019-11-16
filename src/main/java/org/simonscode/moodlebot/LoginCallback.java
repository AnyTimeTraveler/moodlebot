package org.simonscode.moodlebot;

import org.simonscode.moodlebot.callbacks.SendMessageCallback;
import org.simonscode.telegrammenulibrary.CallbackAction;
import org.simonscode.telegrammenulibrary.GotoCallback;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class LoginCallback implements CallbackAction {
    public LoginCallback(GotoCallback mainMenu) {
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        new SendMessageCallback("Please use the command /login <username> <password> in private chat.").execute(bot, callbackQuery);
    }
}
