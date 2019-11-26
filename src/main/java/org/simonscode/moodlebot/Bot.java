package org.simonscode.moodlebot;

import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodlebot.callbacks.AssignmentsCallback;
import org.simonscode.moodlebot.callbacks.CoursesCallback;
import org.simonscode.moodlebot.callbacks.LogoutCallback;
import org.simonscode.moodlebot.callbacks.SendFileCallback;
import org.simonscode.moodlebot.reminders.ReminderManager;
import org.simonscode.moodlebot.reminders.callbacks.RemindersCallback;
import org.simonscode.telegrammenulibrary.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private static SimpleMenu mainMenu = new SimpleMenu();
    private static GotoCallback mainMenuCallback = new GotoCallback(mainMenu);
    private static List<List<MenuButton>> loggedInMarkup = new LinkedList<>();
    private static List<List<MenuButton>> loggedOutMarkup = new LinkedList<>();
    private static HashMap<Long, SendFileCallback> fileUploadCallbacks = new HashMap<>();

    static {
        loggedInMarkup.add(Collections.singletonList(new CallbackButton("Outstanding Assignments", new AssignmentsCallback(mainMenuCallback))));
        loggedInMarkup.add(Collections.singletonList(new CallbackButton("Favorite Courses", new CoursesCallback(mainMenuCallback, true))));
        loggedInMarkup.add(Collections.singletonList(new CallbackButton("Courses", new CoursesCallback(mainMenuCallback, false))));
        loggedInMarkup.add(Collections.singletonList(new CallbackButton("Reminders", new RemindersCallback(mainMenuCallback))));
        loggedInMarkup.add(Collections.singletonList(new CallbackButton("Logout", new LogoutCallback())));
        loggedOutMarkup.add(Collections.singletonList(new CallbackButton("Login", new LoginCallback(mainMenuCallback))));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                State.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        UpdateHook.setCleanupSchedule(
                Duration.of(1, ChronoUnit.HOURS),
                Duration.of(12, ChronoUnit.HOURS));
    }

    public Bot() {
        try {
            State.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ReminderManager.restartReminders(this);

        try {
            State.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addSendFileCallback(Long chatId, SendFileCallback callback) {
        fileUploadCallbacks.put(chatId, callback);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (UpdateHook.onUpdateReceived(this, update)) {
            return;
        }

        final Message message = update.getMessage();
        if (!update.hasMessage()) {
            return;
        }

        if (message.hasDocument()) {
            final SendFileCallback sendFileCallback = fileUploadCallbacks.remove(message.getChatId());
            if (sendFileCallback != null) {
                sendFileCallback.fileSent(this, message);
            }
        }

        if (message.hasText()) {
            if (message.getText().startsWith("/start")) {
                if (!State.instance.users.containsKey(message.getFrom().getId())) {
                    State.instance.users.put(message.getFrom().getId(), new UserData());
                }

                final UserData userData = State.instance.users.get(message.getFrom().getId());
                if (userData != null && userData.getToken() != null && !userData.getToken().isBlank()) {
                    mainMenu.setText("Status: logged in");
                    mainMenu.setMarkup(loggedInMarkup);
                } else {
                    mainMenu.setText("Status: not logged in");
                    mainMenu.setMarkup(loggedOutMarkup);
                }
                try {
                    this.execute(mainMenu.generateSendMessage(message.getChatId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message.getText().startsWith("/login")) {
                String[] parts = message.getText().split(" ");
                if (parts.length != 3) {
                    reply(message, "Invalid usage\n" +
                            "Usage: /login <username> <password>");
                }
                try {
                    UserData userdata = new UserData();
                    userdata.setToken(MoodleAPI.getToken(parts[1], parts[2]));
                    userdata.setUserInfo(MoodleAPI.getUserInfo(userdata.getToken()));
                    reply(message, (userdata.getToken() != null && !userdata.getToken().isEmpty()) ? "Login successful!" : "Login failed!");
                    State.instance.users.put(message.getFrom().getId(), userdata);
                } catch (Exception e) {
                    reply(message, "Login failed!");
                    e.printStackTrace();
                }
            }
        }
    }

    private void reply(Message message, String text) {
        try {
            this.execute(new SendMessage()
                    .setChatId(message.getChatId())
                    .setText(text)
                    .setReplyToMessageId(message.getMessageId())
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "MoodleBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

    @Override
    public void onClosing() {
        super.onClosing();
        try {
            State.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
