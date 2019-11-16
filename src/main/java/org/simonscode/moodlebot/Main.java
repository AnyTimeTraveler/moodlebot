package org.simonscode.moodlebot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        api.registerBot(new Bot());


//        try {
//            final AssignmentReply assignments = Requests.getAssignments("092ff4c911ca8b5d8632a752dc828ab3", Arrays.asList(4340L, 4580L));
//            for (CourseStub courseStub : assignments.getCourses()){
//                System.out.println(courseStub);
//            }
//            HttpResponse<String> response = Unirest.post("https://moodle.hs-emden-leer.de/moodle/webservice/rest/server.php?moodlewsrestformat=json&wsfunction=mod_assign_get_assignments")
//                    .header("Content-Type", "application/x-www-form-urlencoded")
////                    .header("User-Agent", "PostmanRuntime/7.19.0")
//                    .header("Accept", "*/*")
//                    .header("Cache-Control", "no-cache")
//                    .header("Host", "moodle.hs-emden-leer.de")
//                    .header("Accept-Encoding", "gzip, deflate")
//                    .header("Connection", "keep-alive")
//                    .header("cache-control", "no-cache")
//                    .body("courseids%5B0%5D=4340&courseids%5B1%5D=4580&moodlewssettingfilter=true&moodlewssettingfileurl=true&wsfunction=mod_assign_get_assignments&wstoken=092ff4c911ca8b5d8632a752dc828ab3")
////                        "courseids%5B0%5D%3D4340%26courseids%5B1%5D%3D4580%26moodlewssettingfilter=true&moodlewssettingfileurl=true&wsfunction=mod_assign_get_assignments&wstoken=092ff4c911ca8b5d8632a752dc828ab3"
//                    .asString();
//
//            System.out.println(response.getStatus());
//            System.out.println(response.getHeaders());
//            System.out.println(response.getBody());
//        } catch (UnirestException e) {
//            e.printStackTrace();
//        }
    }
}
