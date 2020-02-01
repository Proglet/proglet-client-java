package nl.avans.ti.commands;

import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import nl.avans.ti.Settings;
import nl.avans.ti.util.Command;
import nl.avans.ti.util.Parameter;

import java.util.concurrent.ExecutionException;

@Command(command = "login", description = "Logs in to the central server and stores authentication token")
public class Login implements Runnable {

    @Parameter(index = 0, name = "Login Method", optional = false, description = "Login method to use. Use the LoginServices command to get a list of all login services")
    private String loginMethod = "avans";

    @Override
    public void run() {
        try {
            LoginResponse login = Proglet.login(loginMethod).get();
            if(login.token != null && !login.token.equals("")) {
                Settings.setJwtToken(login.token);
                System.out.println("Logged in");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
