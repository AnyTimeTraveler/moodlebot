package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodleapi.objects.course.CourseContent;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.ParseMode;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CouseModulesCallback implements Callback {
    private final CourseDetailsCallback returnCallback;
    private final CourseContent content;

    public CouseModulesCallback(CourseDetailsCallback returnCallback, CourseContent content) {
        this.returnCallback = returnCallback;
        this.content = content;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        VerticalMenu menu = new VerticalMenu();
        menu.setParseMode(ParseMode.HTML);

        menu.setText("<b>" + content.getName() + "</b>\n" + content.getSummary());

        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        returnCallback.generateCourseModuleCallbacks(callbackQuery, userData, menu, content);

        menu.addButton("Go back", returnCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
