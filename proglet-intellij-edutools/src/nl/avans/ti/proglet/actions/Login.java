package nl.avans.ti.proglet.actions;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import nl.avans.ti.proglet.gui.LoginServiceSelect;
import nl.avans.ti.proglet.gui.Notifier;
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
                if("".equals(response.token))
                    new Notifier().error(e.getProject(), "Unable to login");
                else {
                    PropertiesComponent.getInstance().setValue("nl.avans.ti.proglet.token", response.token);
                    Proglet.token = response.token;

                    new Notifier().notify(e.getProject(), "You are now logged in to the proglet service");
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }


    }
}
