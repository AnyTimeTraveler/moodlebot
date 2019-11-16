package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodlebot.State;
import org.simonscode.telegrammenulibrary.CallbackAction;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class LogoutCallback implements CallbackAction {
    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        State.instance.users.remove(callbackQuery.getFrom().getId());
        new SendMessageCallback("Logout success!").execute(bot, callbackQuery);
    }
}
