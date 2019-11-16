package org.simonscode.moodlebot.reminders;

import org.simonscode.moodlebot.Bot;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.util.*;

public class ReminderManager {
    private static final Timer timer = new Timer();
    private static Bot bot;

    public static void restartReminders(Bot bot) {
        ReminderManager.bot = bot;
        for (UserData user : State.instance.users.values()) {
            List<Reminder> removals = new LinkedList<>();
            for (Reminder reminder : user.getReminders()) {
                if (reminder.getEpochSecond() > Instant.now().getEpochSecond()) {
                    enableReminder(reminder);
                } else {
                    removals.add(reminder);
                }
            }
            user.getReminders().removeAll(removals);
        }
    }

    public static void setReminder(int userId, long courseId, String message, long epochSecond) {
        final Reminder reminder = new Reminder(userId, courseId, message, epochSecond);
        State.instance.users.get(userId).getReminders().add(reminder);
        enableReminder(reminder);
    }

    private static void enableReminder(Reminder reminder) {
        timer.schedule(new ReminderTask(reminder), new Date(Instant.ofEpochSecond(reminder.getEpochSecond()).toEpochMilli()));
    }

    private static class ReminderTask extends TimerTask {
        private final Reminder reminder;

        public ReminderTask(Reminder reminder) {
            this.reminder = reminder;
        }

        @Override
        public void run() {
            try {
                bot.execute(new SendMessage()
                        .setText(reminder.getMessage())
                        .setChatId(String.valueOf(reminder.getUserId()))
                        .enableNotification());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            State.instance.users.get(reminder.getUserId()).getReminders().remove(reminder);
        }
    }
}
