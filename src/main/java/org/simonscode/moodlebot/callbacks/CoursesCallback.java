package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.Requests;
import org.simonscode.moodleapi.objects.course.Course;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.telegrammenulibrary.CallbackAction;
import org.simonscode.telegrammenulibrary.GotoCallback;
import org.simonscode.telegrammenulibrary.SimpleMenu;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CoursesCallback implements CallbackAction {
    private final CallbackAction mainMenuCallback;
    private final boolean onlyShowFavorites;

    public CoursesCallback(CallbackAction mainMenuCallback, boolean onlyShowFavorites) {
        this.mainMenuCallback = mainMenuCallback;
        this.onlyShowFavorites = onlyShowFavorites;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        SimpleMenu menu = new SimpleMenu();
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        try {
            Course[] courses = Requests.getCourses(userData.getToken(), userData.getUserInfo().getUserid());
            if (courses != null) {
                menu.setText((onlyShowFavorites ? "Favorite " : "") + "Courses for " + userData.getUserInfo().getFullname());
                for (Course course : courses) {
                    final boolean isFavorite = userData.getFavoriteCourses().contains(course.getId());
                    if (onlyShowFavorites && !isFavorite) {
                        continue;
                    }
                    menu.addButton(course.getFullname(), new CourseDetailsCallback(new GotoCallback(menu), course));
                    if (!onlyShowFavorites) {
                        menu.addButton(isFavorite ? "⭐️" : "❌", new UpdateFavoriteCallback(userData, isFavorite, course.getId(), this));
                    }
                    menu.nextLine();
                }
            } else {
                menu.setText("Request failed!");
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            menu.setText("There was an error: " + e.getMessage());
        }
        menu.addButton("Go back", mainMenuCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
