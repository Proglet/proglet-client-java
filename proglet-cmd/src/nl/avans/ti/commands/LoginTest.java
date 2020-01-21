package nl.avans.ti.commands;

import nl.avans.ti.LoginResponse;
import nl.avans.ti.Proglet;
import nl.avans.ti.RestClient;
import nl.avans.ti.Settings;
import nl.avans.ti.util.Command;
import nl.avans.ti.util.Parameter;

import java.util.concurrent.ExecutionException;

@Command(command = "logintest", description = "Checks if user is logged in")
public class LoginTest implements Runnable {

    @Override
    public void run() {
        if(Proglet.testLogin())
            System.out.println("You are logged in");
        else
            System.out.println("You are NOT logged in");

    }
}
