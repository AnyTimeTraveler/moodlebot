package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodleapi.objects.SentFileResponse;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.Menu;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class FileSubmittedCallback implements Callback {
    public FileSubmittedCallback(SentFileResponse sentFileRespons, Menu previousMenu) {

    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {

    }
}
