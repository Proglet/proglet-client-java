package nl.avans.ti.commands.courses;

import nl.avans.ti.Course;
import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import nl.avans.ti.util.Command;
import nl.avans.ti.util.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Command(command = "courses list", description = "Lists all courses on the server")
public class ListCommand implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("Available courses:");
            List<Course> courses = Proglet.getCourses().get();
            for(Course course : courses) {
                System.out.printf("%-3d", course.getId());
                if(course.isRegistered())
                    System.out.print("[registered] ");
                else
                    System.out.print("[available]  ");

                System.out.printf("%-10s", course.getName());
                System.out.printf("%-40s", course.getTitle());
                System.out.print(course.getDescription());

                System.out.println();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
