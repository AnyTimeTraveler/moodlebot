package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodlebot.UserData;
import org.simonscode.telegrammenulibrary.Callback;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class UpdateFavoriteCallback implements Callback {
    private final UserData userData;
    private final boolean previousIsFavorite;
    private final long id;
    private final Callback coursesCallback;

    public UpdateFavoriteCallback(UserData userData, boolean previousIsFavorite, long id, Callback coursesCallback) {
        this.userData = userData;
        this.previousIsFavorite = previousIsFavorite;
        this.id = id;
        this.coursesCallback = coursesCallback;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        if (previousIsFavorite) {
            userData.getFavoriteCourses().remove(id);
        } else {
            userData.getFavoriteCourses().add(id);
        }
        coursesCallback.execute(bot, callbackQuery);
    }
}
