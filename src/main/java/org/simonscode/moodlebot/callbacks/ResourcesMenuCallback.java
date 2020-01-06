package org.simonscode.moodlebot.callbacks;

import org.simonscode.moodleapi.objects.course.module.ResourceModule;
import org.simonscode.moodleapi.objects.course.module.content.FileContent;
import org.simonscode.moodleapi.objects.course.module.content.ModuleContent;
import org.simonscode.moodleapi.objects.course.module.content.URLContent;
import org.simonscode.telegrammenulibrary.Callback;
import org.simonscode.telegrammenulibrary.NullCallback;
import org.simonscode.telegrammenulibrary.URLButton;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ResourcesMenuCallback implements Callback {
    private final Callback callback;
    private final String token;
    private final ResourceModule resourceModule;

    public ResourcesMenuCallback(Callback callback, String token, ResourceModule resourceModule) {
        this.callback = callback;
        this.token = token;
        this.resourceModule = resourceModule;
    }

    @Override
    public void execute(AbsSender bot, CallbackQuery callbackQuery) {
        VerticalMenu menu = new VerticalMenu();
        menu.setText(resourceModule.getName());
        for (ModuleContent content : resourceModule.getContents()) {
            if (content instanceof FileContent) {
                FileContent fileContent = (FileContent) content;
                menu.addButton(fileContent.getFilename(), new DownloadCallback(callbackQuery.getMessage().getChatId(), token, fileContent));
            } else if (content instanceof URLContent) {
                URLContent urlContent = (URLContent) content;
                menu.addButton(new URLButton(urlContent.getFilename(), urlContent.getFileurl()));
            } else {
                menu.addButton("Unknown: " + content.getType(), new NullCallback());
            }
        }
        menu.addButton("Go back", callback);
        try {
            bot.execute(menu.generateEditMessage(callbackQuery.getMessage()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
