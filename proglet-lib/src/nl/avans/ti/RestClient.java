package nl.avans.ti;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class RestClient {
    private final String hostname;

    public RestClient(String hostname)
    {
        this.hostname = hostname;
    }


    public String get(String endpoint)
    {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.hostname + "/" + endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + Proglet.token)
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getObject(String endpoint)
    {
        return Jsoner.deserialize(get(endpoint), new JsonObject());
    }

    public JsonArray getArray(String endpoint)
    {
        return Jsoner.deserialize(get(endpoint), new JsonArray());
    }

    public JsonObject post(String endpoint, JsonObject data)
    {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.hostname + "/" + endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Proglet.token)
                .POST(HttpRequest.BodyPublishers.ofString(data.toJson()))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Jsoner.deserialize(response.body(), new JsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
