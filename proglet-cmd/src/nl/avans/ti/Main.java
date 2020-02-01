package nl.avans.ti;

import nl.avans.ti.commands.*;
import nl.avans.ti.util.CommandLine;

public class Main {

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new  CommandLine();
        commandLine.register(Settings.class);
        commandLine.register(Help.class);
        commandLine.register(Setting.class);

        commandLine.register(LoginServices.class);
        commandLine.register(Login.class);
        commandLine.register(LoginTest.class);

        commandLine.parseCommandLine(args);
        commandLine.setOptions();

        Proglet.token = Settings.getJwtToken();

        commandLine.execute(args);

        System.exit(0);
    }
}
