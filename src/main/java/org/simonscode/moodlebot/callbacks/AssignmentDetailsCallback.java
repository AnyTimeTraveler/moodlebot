package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.Requests;
import org.simonscode.moodleapi.objects.assignment.AssignmentStatus;
import org.simonscode.moodleapi.objects.assignment.AssignmentSummary;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.moodlebot.reminders.callbacks.SetReminderCallback;
import org.simonscode.telegrammenulibrary.CallbackAction;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.simonscode.moodlebot.Utils.getTimeLeft;

public class AssignmentDetailsCallback implements CallbackAction {
    private final CallbackAction assignmentsCallback;
    private final AssignmentSummary assignment;

    public AssignmentDetailsCallback(CallbackAction assignmentsCallback, AssignmentSummary assignment) {
        this.assignmentsCallback = assignmentsCallback;
        this.assignment = assignment;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        AssignmentStatus assignmentStatus = null;
        try {
            assignmentStatus = Requests.getAssignmentStatus(userData.getToken(), userData.getUserInfo().getUserid(), assignment.getId());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        VerticalMenu menu = new VerticalMenu();

        menu.setText('*' + assignment.getName() + "*\n" +
                "Max attempts: " + assignment.getMaxattempts() + '\n' +
                "Team submission: " + (assignment.getTeamsubmission() == 1 ? "yes" : "no") + '\n' +
                "Submissions allowed: " + (assignment.getNosubmissions() == 0 ? "yes" : "no") + '\n' +
                "Time left: " + getTimeLeft(assignment.getDuedate()) + '\n'
                + getExtraInfo(assignmentStatus));

        if (assignment.getNosubmissions() == 0) {
            menu.addButton("Set reminder", new SetReminderCallback(this, assignment.getId(), assignment.getName(), assignment.getDuedate()));
            menu.addButton("Submit", new SendFileCallback(assignment.getId(), menu, userData.getToken()));
        }
        menu.addButton("Go back", assignmentsCallback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()).setParseMode(ParseMode.MARKDOWN));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getExtraInfo(AssignmentStatus status) {
        if (status == null || status.getLastattempt() == null) {
            return "Grade: Not graded";
        }
        return "Grade: " + status.getLastattempt().getGradingstatus() + '\n'
                + "Extension: " + getTimeLeft(status.getLastattempt().getExtensionduedate());
    }
}
