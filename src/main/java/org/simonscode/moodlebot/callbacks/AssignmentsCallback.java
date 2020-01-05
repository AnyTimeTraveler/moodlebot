package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodleapi.objects.assignment.AssignmentReply;
import org.simonscode.moodleapi.objects.assignment.AssignmentSummary;
import org.simonscode.moodleapi.objects.assignment.CourseStub;
import org.simonscode.moodleapi.objects.course.Course;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.moodlebot.Utils;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.GotoCallback;
import org.simonscode.telegrammenulibrary.ParseMode;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentsCallback implements Callback {
    private final Callback mainMenuCallback;

    public AssignmentsCallback(Callback mainMenuCallback) {
        this.mainMenuCallback = mainMenuCallback;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        try {
            bot.execute(new SendChatAction().setChatId(callbackQuery.getMessage().getChatId()).setAction(ActionType.TYPING));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        VerticalMenu menu = new VerticalMenu();
        menu.setParseMode(ParseMode.HTML);

        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        AssignmentReply assignments = null;
        try {
            final Course[] courses = MoodleAPI.getCourses(userData.getToken(), userData.getUserInfo().getUserid());
            if (courses != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Assignments for ");
                sb.append(userData.getUserInfo().getFullname());
                sb.append(":\n\n");
                List<Long> courseIds = Arrays.stream(courses).mapToLong(Course::getId).boxed().collect(Collectors.toList());
                assignments = MoodleAPI.getAssignments(userData.getToken(), courseIds);
                for (CourseStub courseStub : assignments.getCourses()) {
                    long current = System.currentTimeMillis();
                    final List<AssignmentSummary> relevantAssignments = Arrays.stream(courseStub.getAssignments())
                            .filter(a -> a.getDuedate() * 1000 > current)
                            .sorted(Comparator.comparingLong(AssignmentSummary::getDuedate))
                            .collect(Collectors.toList());
                    if (!relevantAssignments.isEmpty()) {
                        sb.append("<b>");
                        sb.append(courseStub.getFullname());
                        sb.append(":</b>\n");
                        for (AssignmentSummary assignment : relevantAssignments) {
                            sb.append(" - ");
                            sb.append(assignment.getName());
                            sb.append("\n   Verbleibende Zeit: ");
                            sb.append(Utils.getTimeLeft(assignment.getDuedate()));
                            sb.append("\n");
                            menu.addButton(courseStub.getShortname() + ": " + assignment.getName(), new AssignmentDetailsCallback(new GotoCallback(menu), assignment));
                        }
                    }
                }
                menu.setText(sb.toString());
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
        if (assignments != null) {
            new AddInfoToAssignemntsCallback(mainMenuCallback, assignments).execute(bot, callbackQuery);
        }
    }
}
