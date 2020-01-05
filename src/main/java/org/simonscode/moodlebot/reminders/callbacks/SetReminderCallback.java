package org.simonscode.moodlebot.reminders.callbacks;

import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.moodlebot.reminders.ReminderManager;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SetReminderCallback implements Callback {
    private final Callback assignmentDetailsCallback;
    private final long id;
    private final String name;
    private final long duedate;

    public SetReminderCallback(Callback assignmentDetailsCallback, long id, String name, long duedate) {
        this.assignmentDetailsCallback = assignmentDetailsCallback;
        this.id = id;
        this.name = name;
        this.duedate = duedate;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        final Duration between = Duration.between(Instant.now(), Instant.ofEpochSecond(duedate));

        VerticalMenu menu = new VerticalMenu();

        menu.setText("Please tab the reminders you'd like to add:");

        if (between.toDays() > 7) {
            menu.addButton("7 Days before", new ActuallySetReminderCallback(Instant.ofEpochSecond(duedate).minus(7, ChronoUnit.DAYS).getEpochSecond(), name + " in 7 Days"));
        }
        if (between.toDays() > 3) {
            menu.addButton("3 Days before", new ActuallySetReminderCallback(Instant.ofEpochSecond(duedate).minus(3, ChronoUnit.DAYS).getEpochSecond(), name + " in 3 Days"));
        }
        if (between.toDays() > 1) {
            menu.addButton("1 Day before", new ActuallySetReminderCallback(Instant.ofEpochSecond(duedate).minus(1, ChronoUnit.DAYS).getEpochSecond(), name + " tomorrow"));
        }
        if (between.toHours() > 12) {
            menu.addButton("12 Hours before", new ActuallySetReminderCallback(Instant.ofEpochSecond(duedate).minus(12, ChronoUnit.HOURS).getEpochSecond(), name + " in 12 Hours"));
        }
        if (between.toHours() > 4) {
            menu.addButton("4 Hours before", new ActuallySetReminderCallback(Instant.ofEpochSecond(duedate).minus(4, ChronoUnit.HOURS).getEpochSecond(), name + " in 4 Hours"));
        }
        if (between.toHours() > 1) {
            menu.addButton("1 Hour before", new ActuallySetReminderCallback(Instant.ofEpochSecond(duedate).minus(1, ChronoUnit.HOURS).getEpochSecond(), name + " in 1 Hour"));
        }

        menu.addButton("Go back", assignmentDetailsCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()).setParseMode(ParseMode.HTML));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private class ActuallySetReminderCallback implements Callback {
        private final long epochSecond;
        private final String message;

        private ActuallySetReminderCallback(long epochSecond, String message) {
            this.epochSecond = epochSecond;
            this.message = message;
        }

        @Override
        public void execute(AbsSender bot, CallbackQuery callbackQuery) {
            ReminderManager.setReminder(callbackQuery.getFrom().getId(), id, message, epochSecond);
        }
    }
}
