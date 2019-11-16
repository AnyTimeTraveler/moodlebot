package org.simonscode.moodlebot;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class State {

    public static State instance = new State();

    public Map<Integer, UserData> users = new HashMap<>();

    static void load(@SuppressWarnings("SameParameterValue") String filename) throws IOException {
        final FileReader fileReader = new FileReader(filename);
        instance = new Gson().fromJson(fileReader, State.class);
        fileReader.close();
    }

    static void save(@SuppressWarnings("SameParameterValue") String filename) throws IOException {
        final FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write(new Gson().toJson(instance));
        fileWriter.flush();
        fileWriter.close();
    }

}
