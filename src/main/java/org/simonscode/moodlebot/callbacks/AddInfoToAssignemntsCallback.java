package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodleapi.objects.assignment.AssignmentReply;
import org.simonscode.moodleapi.objects.assignment.AssignmentStatus;
import org.simonscode.moodleapi.objects.assignment.AssignmentSummary;
import org.simonscode.moodleapi.objects.assignment.CourseStub;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.moodlebot.Utils;
import org.simonscode.telegrammenulibrary.Callback;
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

public class AddInfoToAssignemntsCallback implements Callback {
    private final Callback mainMenuCallback;
    private final AssignmentReply reply;

    public AddInfoToAssignemntsCallback(Callback mainMenuCallback, AssignmentReply reply) {
        this.mainMenuCallback = mainMenuCallback;
        this.reply = reply;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        VerticalMenu menu = new VerticalMenu();
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());

        final StringBuilder sb = new StringBuilder();
        sb.append("Assignments for ");
        sb.append(userData.getUserInfo().getFullname());
        sb.append(":\n\n");
        for (CourseStub courseStub : reply.getCourses()) {
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
                    AssignmentStatus assignmentStatus = null;
                    try {
                        assignmentStatus = MoodleAPI.getAssignmentStatus(userData.getToken(), userData.getUserInfo().getUserid(), assignment.getId());
                    } catch (UnirestException e) {
                        e.printStackTrace();
                    }
                    sb.append(" - ");
                    sb.append(assignment.getName());
                    sb.append("\n   Remaining Time: ");
                    sb.append(Utils.getTimeLeft(assignment.getDuedate()));
                    if (assignmentStatus != null && assignmentStatus.getLastattempt() != null) {
                        sb.append("\n   Submissions allowed: ");
                        sb.append(assignmentStatus.getLastattempt().isSubmissionsenabled() ? "yes" : "no");
                        sb.append("\n   Is graded: ");
                        sb.append(assignmentStatus.getLastattempt().isGraded() ? "yes" : "no");
                        sb.append("\n   Grading status: ");
                        sb.append(assignmentStatus.getLastattempt().getGradingstatus());
                        if (assignmentStatus.getLastattempt().getExtensionduedate() != 0) {
                            sb.append("\n   Time left for extension: ");
                            sb.append(Utils.getTimeLeft(assignmentStatus.getLastattempt().getExtensionduedate()));
                        }
                    }
                    if (assignmentStatus != null && assignmentStatus.getFeedback() != null) {
                        sb.append("\n   Feedback Grade: ");
                        sb.append(assignmentStatus.getFeedback().getGradefordisplay());

                        if (assignmentStatus.getFeedback().getGrade() != null) {
                            sb.append("\n   Grade: ");
                            sb.append(assignmentStatus.getFeedback().getGrade().getGrade());
                            sb.append("\n   Grader: ");
                            sb.append(assignmentStatus.getFeedback().getGrade().getGrader());
                            sb.append("\n   Date: ");
                            sb.append(assignmentStatus.getFeedback().getGrade().getTimemodified());
                        }
                    }
                    sb.append("\n");
                    menu.addButton(courseStub.getShortname() + ": " + assignment.getName(), new AssignmentDetailsCallback(new GotoCallback(menu), assignment));
                }
            }
        }
        menu.setText(sb.toString());

        menu.addButton("Go back", mainMenuCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()).setParseMode(ParseMode.HTML));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
