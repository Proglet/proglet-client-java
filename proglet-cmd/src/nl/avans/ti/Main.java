package nl.avans.ti;

import nl.avans.ti.util.CommandLine;

public class Main {

    public static void main(String[] args) throws Exception {
        CommandLine commandLine = new  CommandLine();
        commandLine.registerPackage("nl.avans.ti.commands");
        commandLine.register(Settings.class);


        commandLine.parseCommandLine(args);
        //first set the options from commandline (for the Settings.datafolder)
        commandLine.setOptions();
        //then load the defaults in (from the Settings.datafolder)
        Settings.loadDefaults();
        //then load the settings from commandline again
        commandLine.setOptions();

        Proglet.token = Settings.getJwtToken();

        commandLine.execute(args);

        System.exit(0);
    }
}
