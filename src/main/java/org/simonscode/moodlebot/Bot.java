package org.simonscode.moodlebot;

import org.simonscode.moodleapi.MoodleAPI;
import org.simonscode.moodlebot.callbacks.AssignmentsCallback;
import org.simonscode.moodlebot.callbacks.CoursesCallback;
import org.simonscode.moodlebot.callbacks.LogoutCallback;
import org.simonscode.moodlebot.reminders.ReminderManager;
import org.simonscode.moodlebot.reminders.callbacks.RemindersCallback;
import org.simonscode.telegrammenulibrary.GotoCallback;
import org.simonscode.telegrammenulibrary.UpdateHook;
import org.simonscode.telegrammenulibrary.VerticalMenu;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

public class Bot extends TelegramLongPollingBot {

    private static final String PATHNAME = "moodlebotconfig.json";

    public Bot() {
        try {
            if (new File(PATHNAME).exists()) {
                State.load(PATHNAME);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReminderManager.restartReminders(this);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                State.save(PATHNAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        try {
            State.save(PATHNAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        UpdateHook.onUpdateReceived(this, update);

        final Message message = update.getMessage();
        if (update.hasMessage() && message.hasText()) {
            if (message.getText().startsWith("/start")) {
                if (!State.instance.users.containsKey(message.getFrom().getId())) {
                    State.instance.users.put(message.getFrom().getId(), new UserData());
                }
                VerticalMenu mainMenu = new VerticalMenu();
                GotoCallback mainMenuCallback = new GotoCallback(mainMenu);
                final UserData userData = State.instance.users.get(message.getFrom().getId());
                if (userData != null && userData.getToken() != null && !userData.getToken().isBlank()) {
                    mainMenu.setText("*MoodleBot*\n" +
                            "Status: logged in");
                    mainMenu.addButton("Outstanding Assignments", new AssignmentsCallback(mainMenuCallback));
                    mainMenu.addButton("Favorite Courses", new CoursesCallback(mainMenuCallback, true));
                    mainMenu.addButton("Courses", new CoursesCallback(mainMenuCallback, false));
                    mainMenu.addButton("Reminders", new RemindersCallback(mainMenuCallback));
                    mainMenu.addButton("Logout", new LogoutCallback());
                } else {
                    mainMenu.setText("Status: not logged in");
                    mainMenu.addButton("Login", new LoginCallback(mainMenuCallback));
                }
                try {
                    this.execute(mainMenu.generateSendMessage()
                            .setChatId(message.getChatId())
                    );
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
            State.save(PATHNAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
