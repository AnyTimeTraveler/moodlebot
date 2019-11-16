package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.Requests;
import org.simonscode.moodleapi.objects.assignment.AssignmentReply;
import org.simonscode.moodleapi.objects.assignment.AssignmentSummary;
import org.simonscode.moodleapi.objects.assignment.CourseStub;
import org.simonscode.moodleapi.objects.course.Course;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.moodlebot.Utils;
import org.simonscode.telegrammenulibrary.CallbackAction;
import org.simonscode.telegrammenulibrary.GotoCallback;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AssignmentsCallback implements CallbackAction {
    private final CallbackAction mainMenuCallback;

    public AssignmentsCallback(CallbackAction mainMenuCallback) {
        this.mainMenuCallback = mainMenuCallback;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        VerticalMenu menu = new VerticalMenu();
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        try {
            Course[] courses = Requests.getCourses(userData.getToken(), userData.getUserInfo().getUserid());
            if (courses != null) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Assignments for ");
                sb.append(userData.getUserInfo().getFullname());
                sb.append(":\n\n");
                List<Long> courseIds = Arrays.stream(courses).mapToLong(Course::getId).boxed().collect(Collectors.toList());
                final AssignmentReply assignments = Requests.getAssignments(userData.getToken(), courseIds);
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
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()).setParseMode(ParseMode.HTML));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
