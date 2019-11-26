package org.simonscode.moodlebot.reminders.callbacks;

import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.reminders.Reminder;
import org.simonscode.telegrammenulibrary.Callback;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class RemoveReminderCallback implements Callback {
    private final Callback remindersCallback;
    private final Reminder reminder;

    public RemoveReminderCallback(Callback remindersCallback, Reminder reminder) {
        this.remindersCallback = remindersCallback;
        this.reminder = reminder;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        State.instance.users.get(callbackQuery.getFrom().getId()).getReminders().remove(reminder);
        remindersCallback.execute(bot, callbackQuery);
    }
}
