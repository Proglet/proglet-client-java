import nl.avans.ti.Proglet;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class testExerciseSubmission {

    @Test
    public void testExerciseSubmission() throws IOException, ExecutionException, InterruptedException {
        Proglet.host = "http://localhost:5000";
        Proglet.token = Files.readString(Paths.get(System.getenv("APPDATA") + "/proglet/token"));

        Proglet.submitExercise(1, "if/001.GreaterNumber", Files.readAllBytes(Paths.get("C:\\Users\\johan\\Documents\\proglet\\OGP0 2020-2021\\if-001.GreaterNumber_main.zip"))).get();
    }

}
