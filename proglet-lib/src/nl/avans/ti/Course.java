package nl.avans.ti;

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
}
