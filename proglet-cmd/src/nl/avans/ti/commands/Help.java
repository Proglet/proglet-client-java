package nl.avans.ti.commands;

import nl.avans.ti.util.Command;

@Command(command = "help", description = "Shows a list of commands and arguments")
public class Help implements Runnable {
    @Override
    public void run() {
        System.out.println("Commands: ");
        System.out.println("TODO");
    }
}
