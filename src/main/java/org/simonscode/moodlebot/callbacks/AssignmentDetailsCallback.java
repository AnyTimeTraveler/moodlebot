package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodleapi.objects.assignment.AssignmentStatus;
import org.simonscode.moodleapi.objects.assignment.AssignmentSummary;
import org.simonscode.moodlebot.State;
import org.simonscode.moodlebot.UserData;
import org.simonscode.moodlebot.reminders.callbacks.SetReminderCallback;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.simonscode.moodlebot.Utils.getTimeLeft;

public class AssignmentDetailsCallback implements Callback {
    private final Callback assignmentsCallback;
    private final AssignmentSummary assignment;

    public AssignmentDetailsCallback(Callback assignmentsCallback, AssignmentSummary assignment) {
        this.assignmentsCallback = assignmentsCallback;
        this.assignment = assignment;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        final UserData userData = State.instance.users.get(callbackQuery.getFrom().getId());
        AssignmentStatus assignmentStatus = null;
        try {
            assignmentStatus = MoodleAPI.getAssignmentStatus(userData.getToken(), userData.getUserInfo().getUserid(), assignment.getId());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        VerticalMenu menu = new VerticalMenu();

        menu.setText("<b>" + assignment.getName() + "</b>\n" +
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
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()).setParseMode(ParseMode.HTML));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getExtraInfo(AssignmentStatus status) {
        if (status == null || status.getLastattempt() == null) {
            return "Status: unknown";
        }
        return "Status: " + status.getLastattempt().getGradingstatus() + '\n' +
                "Locked:" + status.getLastattempt().isLocked() + '\n' +
                "Extension: " + (status.getLastattempt().getExtensionduedate() == 0 ? "no" : "yes") + '\n';
    }
}
