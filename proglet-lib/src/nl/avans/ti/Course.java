package nl.avans.ti;

import com.github.cliftonlabs.json_simple.JsonObject;
import nl.avans.ti.util.Unzip;

import javax.swing.filechooser.FileSystemView;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class Course {
    private long id;
    private String name;
    private String title;
    private String description;
    private String curriculum;
    private boolean registered;

    public Course(long courseId, String name, String title, String description, String curriculum, boolean registered) {
        this.id  = courseId;
        this.name = name;
        this.title = title;
        this.description = description;
        this.registered = registered;
        this.curriculum = curriculum;
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

    public String getCurriculum() {
        return this.curriculum;
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
            JsonObject loginResult = new RestClient(Proglet.host).post("api/Courses/unregister/" + id);
        });
    }

    public CompletableFuture<Void> enroll() {
        return CompletableFuture.runAsync(() ->
        {
            JsonObject loginResult = new RestClient(Proglet.host).post("api/Courses/Enroll/" + id);
        });
    }

    public Path projectPath()
    {
        return Paths.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/proglet/" + this.name + " " + this.curriculum + "/");
    }


    public CompletableFuture<Void> downloadProject() {
        return CompletableFuture.runAsync(() ->
        {
            JsonObject info = new RestClient(Proglet.host).getObject("api/Courses/" + id);
            byte[] data = new RestClient(Proglet.host).getFile("api/Courses/DownloadMainProject/" + id);

            Unzip.unzip(data, projectPath());
        });
    }




}
