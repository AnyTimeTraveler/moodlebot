package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodleapi.objects.course.Course;
import org.simonscode.moodleapi.objects.course.CourseContent;
import org.simonscode.moodleapi.objects.course.module.Module;
import org.simonscode.moodleapi.objects.course.module.*;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CourseDetailsCallback implements Callback {
    private final Callback coursesCallback;
    private final Course course;

    public CourseDetailsCallback(Callback coursesCallback, Course course) {
        this.coursesCallback = coursesCallback;
        this.course = course;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        try {
            bot.execute(new SendChatAction().setChatId(callbackQuery.getMessage().getChatId()).setAction(ActionType.TYPING));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        VerticalMenu menu = new VerticalMenu();

        menu.setText("<b>" + course.getFullname() + "</b>\n" +
                "Studenten: " + course.getEnrolledusercount());
        try {
            final CourseContent[] courseDetails = MoodleAPI.getCourseDetails(userData.getToken(), course.getId());
            for (CourseContent courseDetail : courseDetails) {
                for (Module module : courseDetail.getModules()) {
                    if (module instanceof LabelModule) {
                        LabelModule labelModule = (LabelModule) module;
                        menu.addButton(labelModule.getName(), new TextMenuCallback(this, labelModule.getDescription()));
                    } else if (module instanceof ForumModule || module instanceof UrlModule) {
                        menu.addButton(module.getName(), new TextMenuCallback(this, "URL:" + module.getUrl()));
                    } else if (module instanceof ResourceModule) {
                        if (((ResourceModule) module).getContents().size() == 1) {
                            menu.addButton(module.getName(), new DownloadCallback(callbackQuery.getMessage().getChatId(), userData.getToken(), ((ResourceModule) module).getContents().get(0)));
                        } else {
                            menu.addButton(module.getName(), new ResourcesMenuCallback(this, (ResourceModule) module));
                        }
                    } else if (module instanceof AssignmentModule) {
                        menu.addButton(module.getName(), new TextMenuCallback(this, module.toString()));
                    } else if (module instanceof ChoiceGroupModule) {
                        menu.addButton(module.getName(), new TextMenuCallback(this, module.toString()));
                    } else if (module instanceof ChoiceModule) {
                        menu.addButton(module.getName(), new TextMenuCallback(this, module.toString()));
                    } else {
                        menu.addButton("Unknown:" + module.getName(), new TextMenuCallback(this, module.toString()));
                    }
                }
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        menu.addButton("Go back", coursesCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()).setParseMode(ParseMode.HTML));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
