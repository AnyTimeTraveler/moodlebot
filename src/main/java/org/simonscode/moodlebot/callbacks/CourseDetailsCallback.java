package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodleapi.objects.course.Course;
import org.simonscode.telegrammenulibrary.CallbackAction;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CourseDetailsCallback implements CallbackAction {
    private final CallbackAction coursesCallback;
    private final Course course;

    public CourseDetailsCallback(CallbackAction coursesCallback, Course course) {
        this.coursesCallback = coursesCallback;
        this.course = course;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        VerticalMenu menu = new VerticalMenu();

        menu.setText('*' + course.getFullname() + "*\n" +
                "Studenten: " + course.getEnrolledusercount() + '\n' +
                "Zusammefassung:\n" +
                course.getSummary() + '\n');

        menu.addButton("Go back", coursesCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()).setParseMode(ParseMode.MARKDOWN));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
