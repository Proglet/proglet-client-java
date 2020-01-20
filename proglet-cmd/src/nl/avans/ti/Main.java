package nl.avans.ti;

import nl.avans.ti.commands.Help;
import nl.avans.ti.commands.Login;
import nl.avans.ti.commands.LoginServices;
import nl.avans.ti.util.CommandLine;

public class Main {

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new  CommandLine();
        commandLine.register(Settings.class);
        commandLine.register(Help.class);
        commandLine.register(LoginServices.class);
        commandLine.register(Login.class);

        commandLine.execute(args);
    }

}
