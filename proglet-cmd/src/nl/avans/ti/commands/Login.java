package nl.avans.ti.commands;

import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import nl.avans.ti.util.Command;
import nl.avans.ti.util.Parameter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Command(command = "login", description = "Logs in to the central server and stores authentication token")
public class Login implements Runnable {

    @Parameter(index = 0, name = "login method", optional = false)
    private String loginMethod = "avans";

    @Override
    public void run() {
        try {
            LoginResponse login = Proglet.login(loginMethod).get();

            System.out.println(login);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
