package nl.avans.ti;

public class Course {
    private long courseId;
    private String name;
    private String title;
    private String description;

    public Course(long courseId, String name, String title, String description) {
        this.courseId = courseId;
        this.name = name;
        this.title = title;
        this.description = description;
    }

    public long getCourseId() {
        return courseId;
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
}
