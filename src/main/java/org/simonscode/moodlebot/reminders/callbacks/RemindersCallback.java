package org.simonscode.moodlebot.reminders.callbacks;

import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.moodlebot.reminders.Reminder;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class RemindersCallback implements Callback {
    private final Callback mainMenuCallback;

    public RemindersCallback(Callback mainMenuCallback) {
        this.mainMenuCallback = mainMenuCallback;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        VerticalMenu menu = new VerticalMenu();
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        menu.setText("You have " + userData.getReminders().size() + " reminders set.\n\nClick to remove.");

        for (Reminder reminder : userData.getReminders()) {
            menu.addButton(reminder.getMessage(), new RemoveReminderCallback(this, reminder));
        }

        menu.addButton("Go back", mainMenuCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
