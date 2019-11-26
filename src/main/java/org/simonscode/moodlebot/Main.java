package org.simonscode.moodlebot;

import org.simonscode.moodleapi.MoodleAPI;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) throws TelegramApiRequestException {
        MoodleAPI.setMoodleAddress("https://moodle.hs-emden-leer.de/moodle");
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        api.registerBot(new Bot());
    }
}
