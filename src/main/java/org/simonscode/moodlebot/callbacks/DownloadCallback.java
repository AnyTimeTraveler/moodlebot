package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodleapi.objects.course.module.content.ModuleContent;
import org.simonscode.telegrammenulibrary.Callback;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DownloadCallback implements Callback {
    private final Long chatId;
    private final String token;
    private final ModuleContent moduleContent;

    public DownloadCallback(Long chatId, String token, ModuleContent moduleContent) {
        this.chatId = chatId;
        this.token = token;
        this.moduleContent = moduleContent;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        try {
            bot.execute(new SendChatAction(chatId, ActionType.UPLOADDOCUMENT.toString()));
            bot.execute(new SendDocument()
                    .setDocument(moduleContent.getFilename(), MoodleAPI.downloadFile(token, moduleContent.getFileurl()))
                    .setChatId(chatId));
        } catch (TelegramApiException | UnirestException e) {
            e.printStackTrace();
        }
    }
}
