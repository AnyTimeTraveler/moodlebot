package org.simonscode.moodlebot.reminders;

import lombok.Data;

@Data
public class Reminder {
    private final int userId;
    private final long courseId;
    private final String message;
    private final long epochSecond;
}
