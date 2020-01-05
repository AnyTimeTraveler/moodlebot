package org.simonscode.moodlebot.callbacks;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodleapi.objects.SentFileResponse;
import org.simonscode.moodlebot.Bot;
import org.simonscode.moodlebot.Utils;
import org.simonscode.telegrammenulibrary.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;

public class SendFileCallback implements Callback {
    private final long assignmentId;
    private final Menu previousMenu;
    private final String token;
    private Message statusMessage;

    public SendFileCallback(long assignmentId, Menu previousMenu, String token) {
        this.assignmentId = assignmentId;
        this.previousMenu = previousMenu;
        this.token = token;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        Bot.addSendFileCallback(callbackQuery.getMessage().getChatId(), this);
        try {
            statusMessage = (Message) bot.execute(new EditMessageText()
                    .setMessageId(callbackQuery.getMessage().getMessageId())
                    .setChatId(callbackQuery.getMessage().getChatId())
                    .setText("Ready to receive file...")
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void fileSent(AbsSender bot, Message message, String botToken) {
        try {
            VerticalMenu menu = new VerticalMenu();
            menu.setParseMode(ParseMode.HTML);

            try {
                InputStream is = Utils.getFileInputStream(bot, message.getDocument().getFileId(), botToken);
                final SentFileResponse[] sentFileResponses = MoodleAPI.sendFile(token, is, message.getDocument().getFileName());
                if (sentFileResponses.length == 1) {
                    MoodleAPI.assignFileToAssignment(token, assignmentId, sentFileResponses[0].getItemid());
                    menu.setText("File submitted successfully!");
                }
            } catch (UnirestException | IOException e) {
                e.printStackTrace();
                menu.setText("Something went wrong!\n" + e.getMessage());
            }
            menu.addButton("Go back", new GotoCallback(previousMenu));
            bot.execute(menu.generateEditMessage(statusMessage));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
