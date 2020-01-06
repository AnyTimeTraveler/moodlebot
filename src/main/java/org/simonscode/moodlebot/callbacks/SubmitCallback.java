package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodleapi.objects.assignment.AssignmentSummary;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Calendar;

public class SubmitCallback implements Callback {
    private final Callback callback;
    private final AssignmentSummary assignment;
    private final String token;

    public SubmitCallback(Callback callback, AssignmentSummary assignment, String token) {
        this.callback = callback;
        this.assignment = assignment;
        this.token = token;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        VerticalMenu menu = new VerticalMenu();

        Calendar.getInstance();

        menu.addButton("Go back", callback);
    }
}
