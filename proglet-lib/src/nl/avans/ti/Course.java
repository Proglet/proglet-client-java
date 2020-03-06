package nl.avans.ti;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.util.concurrent.CompletableFuture;

public class Course {
    private long id;
    private String name;
    private String title;
    private String description;
    private boolean registered;

    public Course(long courseId, String name, String title, String description, boolean registered) {
        this.id  = courseId;
        this.name = name;
        this.title = title;
        this.description = description;
        this.registered = registered;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", registered=" + registered +
                '}';
    }

    public CompletableFuture<Void> unregister() {
        return CompletableFuture.runAsync(() ->
        {
            JsonObject loginResult = new RestClient(Proglet.host).post("api/Courses/unregister/" + id, null);
        });
    }

    public CompletableFuture<Void> enroll() {
        return CompletableFuture.runAsync(() ->
        {
            JsonObject loginResult = new RestClient(Proglet.host).post("api/Courses/Enroll/" + id, null);
        });
    }
}
