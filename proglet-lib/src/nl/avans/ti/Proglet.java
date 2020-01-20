package nl.avans.ti;

import com.github.cliftonlabs.json_simple.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class Proglet {
    public static String host = ""; //TODO: non-static?

    public static CompletableFuture<List<String>> loginServices()
    {
        return CompletableFuture.supplyAsync(new Supplier<List<String>>() {
            @Override
            public List<String> get() {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:19967/api/login"))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    JsonArray options = Jsoner.deserialize(response.body(), new JsonArray());
                    List<String> loginOptions = new ArrayList<>();
                    options.forEach(s -> loginOptions.add((String)s));
                    return loginOptions;

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new ArrayList<String>();
            }
        });
    }


    public static CompletableFuture<LoginResponse> login(String service)
    {
        return CompletableFuture.supplyAsync(new Supplier<LoginResponse>() {
            @Override
            public LoginResponse get() {

                try {
                    JsonObject post = new JsonObject();
                    post.put("loginservice", service);

                    HttpClient client = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(10))
                            .build();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(host + "/api/login/login"))
                            .timeout(Duration.ofSeconds(10))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(post.toJson()))
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    JsonObject loginResult = Jsoner.deserialize(response.body(), new JsonObject());

                    switch((String)loginResult.get("result"))
                    {
                        case "oauth":
                            Application.launch(LoginGui.class, (String)loginResult.get("url"));
                            //after browser closed...
                            post = new JsonObject();
                            post.put("oauth_token", LoginGui.oauth_token);
                            post.put("oauth_verifier", LoginGui.oauth_verifier);
                            post.put("jwt", (String)loginResult.get("jwt"));
                            post.put("loginservice", service);

                            request = HttpRequest.newBuilder()
                                    .uri(URI.create(host + "/api/login/oauthfinish"))
                                    .timeout(Duration.ofSeconds(10))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString(post.toJson()))
                                    .build();
                            response = client.send(request, HttpResponse.BodyHandlers.ofString());

                            JsonObject finalResult = Jsoner.deserialize(response.body(), new JsonObject());
                            return new LoginResponse((String)finalResult.get("jwt"));

                        default:
                            System.err.println("Unsupported login option");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                return new LoginResponse("");
            }
        });
    }


}
