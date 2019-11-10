package org.simonscode.moodlebot;

import org.simonscode.telegrammenulibrary.UpdateHook;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingBot {

    public Bot() {

    }

    @Override
    public void onUpdateReceived(Update update) {
        UpdateHook.onUpdateReceived(this, update);
    }

    @Override
    public String getBotUsername() {
        return "MoodleBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }
}
