package org.simonscode.moodlebot;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

public class Utils {
    public static String getTimeLeft(long duedate) {
        final Duration between = Duration.between(Instant.now(), Instant.ofEpochSecond(duedate));

        StringBuilder sb = new StringBuilder();
        if (between.toDays() > 0) {
            sb.append(between.toDaysPart());
            sb.append(" Days ");
        }

        if (between.toHoursPart() < 10) {
            sb.append('0');
        }
        sb.append(between.toHoursPart());
        sb.append(":");

        if (between.toMinutesPart() < 10) {
            sb.append('0');
        }
        sb.append(between.toMinutesPart());
        sb.append(":");

        if (between.toSecondsPart() < 10) {
            sb.append('0');
        }
        sb.append(between.toSecondsPart());

        return sb.toString();
    }

    public static InputStream getFileInputStream(AbsSender bot, String fileId, String botToken) throws TelegramApiException, IOException {
        final File sentFile = bot.execute(new GetFile().setFileId(fileId));
        return new URL(sentFile.getFileUrl(botToken)).openStream();
    }
}
