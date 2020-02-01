package nl.avans.ti;

import com.github.cliftonlabs.json_simple.*;
import javafx.application.Application;
import nl.avans.ti.model.LoginGui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Proglet {
    public static String host = ""; //TODO: non-static?
    public static String token = "";


    public static CompletableFuture<List<String>> loginServices()
    {
        return CompletableFuture.supplyAsync(new Supplier<List<String>>() {
            @Override
            public List<String> get() {
                JsonArray options = new RestClient(Proglet.host).getArray("api/login");
                List<String> loginOptions = new ArrayList<>();
                options.forEach(s -> loginOptions.add((String)s));
                return loginOptions;
            }
        });
    }


    public static CompletableFuture<LoginResponse> login(String service)
    {
        return CompletableFuture.supplyAsync(new Supplier<LoginResponse>() {
            @Override
            public LoginResponse get() {

                JsonObject postData = new JsonObject();
                postData.put("loginservice", service);
                JsonObject loginResult = new RestClient(Proglet.host).post("api/login/login", postData);


                switch((String)loginResult.get("result"))
                {
                    case "oauth":
                        Application.launch(LoginGui.class, (String)loginResult.get("url"));
                        //after browser closed...
                        postData = new JsonObject();
                        postData.put("oauth_token", LoginGui.oauth_token);
                        postData.put("oauth_verifier", LoginGui.oauth_verifier);
                        postData.put("jwt", (String)loginResult.get("jwt"));
                        postData.put("loginservice", service);

                        JsonObject finalResult = new RestClient(Proglet.host).post("api/login/oauthfinish", postData);
                        return new LoginResponse((String)finalResult.get("jwt"));

                    default:
                        System.err.println("Unsupported login option");
                }


                return new LoginResponse("");
            }
        });
    }

    public static boolean testLogin() {
        return new RestClient(Proglet.host).get("api/login/testlogin").equals("Logged in!");
    }


    public static List<Course> getCourses() {
        JsonArray coursesJson = new RestClient(Proglet.host).getArray("api/Courses");

        return coursesJson.stream().map(o -> {
            JsonObject jo = (JsonObject)o;

            return new Course((Long)jo.get("id"), (String)jo.get("name"), (String)jo.get("title"), (String)jo.get("description"));
        }).collect(Collectors.toList());

    }
}
