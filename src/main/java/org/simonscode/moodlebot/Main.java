package org.simonscode.moodlebot;

import org.apache.commons.codec.binary.Base64;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        api.registerBot(new Bot());
    }

    private static void run(long start, long end) {
        new Thread(() -> {
            for (long i = start; i < end; i++) {
                final String s = Base64.encodeBase64String(ByteBuffer.allocate(8).putLong(start).array());
                if (s.charAt(s.length() - 1) != '=') {
                    System.out.println(i);
                    System.out.println(s);
                    System.exit(0);
                }
            }
        }).start();
    }
}
