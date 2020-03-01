package nl.avans.ti.proglet.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class Login extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        Proglet.host = "http://localhost:5000";

        try {
            LoginResponse response = Proglet.login("avans").get();



        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }

    }
}
