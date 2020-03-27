package nl.avans.ti.commands;

import nl.avans.ti.util.Command;
import nl.avans.ti.util.CommandLine;
import nl.avans.ti.util.Parameter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Command(command = "help", description = "Shows a list of commands and arguments")
public class Help implements Runnable {
    private CommandLine commandLine; //is automatically set

    @Override
    public void run() {
        System.out.println("Commands: ");
        List<Class> commands = commandLine.getCommands();

        for(Class c : commands) {
            if(c.getDeclaredAnnotation(Command.class) == null)
                continue;
            Command command = (Command)c.getDeclaredAnnotation(Command.class); //TODO: should this be casted?

            //gather all the parameters, and add <> around the non-optional ones, and [] around the optional ones
            String parameters = String.join(" ",
                Arrays.stream(c.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Parameter.class))
                        .map(field -> field.getAnnotation(Parameter.class))
                        .sorted(Comparator.comparingInt(f -> f.index()))
                        .map(field -> {
                            String desc = field.name();
                            if(field.optional())
                                desc = "[" + desc + "]";
                            else
                                desc = "<" + desc + ">";
                            return desc;
                        })
                        .collect(Collectors.toList()));

            //print the command
            System.out.println("proglet " + command.command() + " " + parameters);
            System.out.println(command.description());

            //print help on parameters
            Arrays.stream(c.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Parameter.class))
                    .map(field -> field.getAnnotation(Parameter.class))
                    .sorted(Comparator.comparingInt(f -> f.index()))
                    .forEach(f -> {
                        System.out.println("Parameter " + f.name());
                        System.out.println("          " + f.description());
                    });

            System.out.println("");

        }

    }
}
