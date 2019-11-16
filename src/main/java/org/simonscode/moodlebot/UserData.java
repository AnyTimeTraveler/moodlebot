package org.simonscode.moodlebot;

import lombok.Data;
import org.simonscode.moodleapi.objects.UserInfo;
import org.simonscode.moodlebot.reminders.Reminder;

import java.util.LinkedList;
import java.util.List;

@Data
public class UserData {
    private String token = "";
    private UserInfo userInfo;
    private List<Long> favoriteCourses = new LinkedList<>();
    private List<Reminder> reminders = new LinkedList<>();
}
