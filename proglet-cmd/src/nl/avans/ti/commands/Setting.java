package nl.avans.ti.commands;

import nl.avans.ti.Settings;
import nl.avans.ti.util.Command;
import nl.avans.ti.util.Option;
import nl.avans.ti.util.Parameter;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Command(command = "setting", description = "Allows you to change default settings")
public class Setting implements Runnable {
    @Parameter(index = 0, name = "name", optional = false, description = "the name of the settings to change")
    private String name = "";

    @Parameter(index = 1, name = "value", optional = true, description = "the value on what to set it to. Leave empty to unset")
    private String value = "";

    @Override
    public void run() {
        Supplier<Stream<Option>> options = () -> Stream.concat(
                Arrays.stream(Settings.class.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Option.class)).map(f -> f.getAnnotation(Option.class)),
                Arrays.stream(Settings.class.getDeclaredMethods()).filter(f -> f.isAnnotationPresent(Option.class)).map(f -> f.getAnnotation(Option.class)));

        Option option = options.get().filter(f -> f.name().equals(this.name)).findFirst().orElse(null);

        if(option == null) {
            System.out.println("Error: Setting " + this.name + " not found. Valid settings are ");
            options.get().forEach(o -> System.out.println("- " + o.name()));
            return;
        }

        if(value.equals(""))
            Settings.clearDefault(name);
        else
            Settings.storeDefault(name, value);

    }
}
