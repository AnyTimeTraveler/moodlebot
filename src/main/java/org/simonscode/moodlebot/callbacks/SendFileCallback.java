package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodlebot.Bot;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.Menu;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendFileCallback implements Callback {
    private final long assignmentId;
    private final Menu previousMenu;
    private final String token;

    public SendFileCallback(long assignmentId, Menu previousMenu, String token) {
        this.assignmentId = assignmentId;
        this.previousMenu = previousMenu;
        this.token = token;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {

        Bot.addSendFileCallback(callbackQuery.getMessage().getChatId(), this);
        try {
            bot.execute(new EditMessageText()
                    .setMessageId(callbackQuery.getMessage().getMessageId())
                    .setChatId(callbackQuery.getMessage().getChatId())
                    .setText("Ready to receive file...")
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void fileSent(AbsSender bot, Message message) {
        try {
            final File sentFile = bot.execute(new GetFile().setFileId(message.getDocument().getFileId()));
//            MoodleAPI.sendFile(token, );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
