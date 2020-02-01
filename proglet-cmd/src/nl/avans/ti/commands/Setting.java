package nl.avans.ti.commands;

import nl.avans.ti.util.Command;
import nl.avans.ti.util.Parameter;

@Command(command = "setting", description = "Allows you to change default settings")
public class Setting implements Runnable {
    @Parameter(index = 0, name = "name", optional = false, description = "the name of the settings to change")
    private String name = "";

    @Parameter(index = 1, name = "value", optional = true, description = "the value on what to set it to. Leave empty to unset")
    private String value = "";

    @Override
    public void run() {

    }
}
