package nl.avans.ti;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import nl.avans.ti.model.LoginGui;
import nl.avans.ti.model.LoginMethod;
import nl.avans.ti.tinyhttp.HttpServer;
import nl.avans.ti.util.MultiPartBodyPublisher;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Proglet {
    public static String host = ""; //TODO: non-static?
    public static String token = "";


    public static CompletableFuture<List<LoginMethod>> loginServices()
    {
        return CompletableFuture.supplyAsync(new Supplier<List<LoginMethod>>() {
            @Override
            public List<LoginMethod> get() {
                JsonArray options = new RestClient(Proglet.host).getArray("api/login");
                List<LoginMethod> loginOptions = new ArrayList<>();
                options.forEach(s -> loginOptions.add(new LoginMethod((String)((JsonObject)s).get("name"), (String)((JsonObject)s).get("type"))));
                return loginOptions;
            }
        });
    }


    public static CompletableFuture<LoginResponse> login(String service)
    {
        return CompletableFuture.supplyAsync(new Supplier<LoginResponse>() {
            @Override
            public LoginResponse get() {
                String type = "";

                try {
                    List<LoginMethod> loginMethods = loginServices().get();
                    type = loginMethods.stream().filter(s -> s.getName().equals(service)).findFirst().orElse(null).getType();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                if(type.equals(""))
                    return null;

                //TODO: make sure 5702 is not taken
                JsonObject postData = new JsonObject();
                postData.put("loginservice", service);
                if(type.toLowerCase().equals("oauth"))
                    postData.put("return", "http://localhost:5702/");
                JsonObject loginResult = new RestClient(Proglet.host).post("api/login/login", postData);

                if(loginResult == null) {
                    System.out.println("Could not connect to proglet server");
                    return null;
                }

                switch((String)loginResult.get("result"))
                {
                    case "oauth":
                        Lock lock = new ReentrantLock();
                        Condition blocker = lock.newCondition();
                        lock.lock();

                        try {
                            Desktop.getDesktop().browse(new URI((String)loginResult.get("url")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        postData.clear();

                        HttpServer server = new HttpServer(5702);
                        server.on("/", params -> {
                            if(!params.containsKey("oauth_token") || !params.containsKey("oauth_verifier"))
                                return "Error, parameters not set";
                            postData.put("oauth_token", params.get("oauth_token"));
                            postData.put("oauth_verifier", params.get("oauth_verifier"));
                            lock.lock();
                            blocker.signal();
                            lock.unlock();
                            return "You are now logged in. You can close this browser<script>setTimeout('window.close()', 2000);</script>";
                        });
                        server.start();

                        try {
                            blocker.await();
                            lock.unlock();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            server.stop();
                        }


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


    public static CompletableFuture<List<Course>> getCourses() {
        return CompletableFuture.supplyAsync(new Supplier<>() {
            @Override
            public List<Course> get() {
                JsonArray coursesJson = new RestClient(Proglet.host).getArray("api/Courses");

                return coursesJson.stream().map(o -> {
                    JsonObject jo = (JsonObject) o;

                    return new Course(((BigDecimal) jo.get("id")).longValue(), (String) jo.get("name"), (String) jo.get("title"), (String) jo.get("description"), (String)jo.get("curriculum"), (Boolean)jo.get("registered"));
                }).collect(Collectors.toList());
            }
        });
    }

    public static CompletableFuture<Void> submitExercise(int courseId, String exerciseName, byte[] zipData) {
        return CompletableFuture.runAsync(() -> {

            MultiPartBodyPublisher publisher = new MultiPartBodyPublisher()
                    .addPart("CourseId", courseId+"")
                    .addPart("ExerciseName", exerciseName)
                    .addPart("Data", () -> new ByteArrayInputStream(zipData), "project.zip", "application/zip");


            JsonObject data = new RestClient(Proglet.host).post("api/Submissions/Submit", publisher);
        });
    }
}
