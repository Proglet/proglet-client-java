package nl.avans.ti.commands;

import nl.avans.ti.Proglet;
import nl.avans.ti.model.LoginMethod;
import nl.avans.ti.util.Command;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Command(command = "loginservices", description = "Shows the different login services available on the server")
public class LoginServices implements Runnable {

    @Override
    public void run() {
        try {
            List<LoginMethod> services  = Proglet.loginServices().get();

            System.out.println("Login services available: ");
            for(LoginMethod service : services) {
                System.out.println(" - " + service.getName() + "(" + service.getType() + ")");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
