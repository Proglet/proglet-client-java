package nl.avans.ti.commands;

import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import nl.avans.ti.util.Command;
import nl.avans.ti.util.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Command(command = "loginservices", description = "Shows the different login services available on the server")
public class LoginServices implements Runnable {

    @Override
    public void run() {
        try {
            List<String> services  = Proglet.loginServices().get();

            System.out.println("Login services available: ");
            for(String service : services) {
                System.out.println(" - " + service);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
