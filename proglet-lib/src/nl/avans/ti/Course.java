package nl.avans.ti;

import com.github.cliftonlabs.json_simple.JsonObject;

import javax.swing.filechooser.FileSystemView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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

    public CompletableFuture<Void> downloadProject() {
        return CompletableFuture.runAsync(() ->
        {
            byte[] data = new RestClient(Proglet.host).getFile("api/Courses/DownloadMainProject/" + id);
            ZipInputStream zipFile = new ZipInputStream(new ByteArrayInputStream(data));
            try {
                byte buffer[] = new byte[1024];
                File destDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/proglet/" + id + "/");

                Files.createDirectories(Paths.get(destDir.getPath()));

                ZipEntry entry = zipFile.getNextEntry();
                while(entry != null)
                {
                    File newFile = newFile(destDir, entry);


                    if(!Files.exists(Paths.get(newFile.getParent())))
                        Files.createDirectories(Paths.get(newFile.getParent()));

                    if(entry.isDirectory())
                    {
                        entry = zipFile.getNextEntry();
                        continue;
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zipFile.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    } catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                    entry = zipFile.getNextEntry();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }

    //TODO: move to util
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
