package nl.avans.ti.proglet.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import nl.avans.ti.proglet.gui.LoginServiceSelect;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class Login extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LoginServiceSelect loginServiceSelect = new LoginServiceSelect();
        if(loginServiceSelect.showAndGet()) {

            try {
                String selectedService = loginServiceSelect.loginService.getSelectedValue();
                LoginResponse response = Proglet.login(selectedService).get();
                PropertiesComponent.getInstance().setValue("nl.avans.ti.proglet.token", response.token);
                Proglet.token = response.token;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }


    }
}
