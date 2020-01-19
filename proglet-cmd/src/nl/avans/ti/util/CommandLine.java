package nl.avans.ti.util;

import nl.avans.ti.commands.Help;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandLine {
    private List<Class> commands = new ArrayList<>();


    public void execute(String[] args) throws Exception {
        String command = null;
        for(int i = 0; i < args.length; i++) {
            if(args[i].startsWith("-") && !args[i].contains("="))
                i++; //skip the next item if the current parameter is, for instance, "-i", "--input", but not if it is "--input=asd"
            command = args[i];
            break;
        }
        if(command == null) {
            System.err.println("Error parsing command line, no command found");
            return;
        }


        for(Class c : commands) {
            Command commandAnnotation = (Command)c.getDeclaredAnnotation(Command.class); //TODO: should this be casted?
            if(commandAnnotation.command().equals(command)) {
                if(!Runnable.class.isAssignableFrom(c))
                    throw new Exception("Command " + c.getName() + " is not a Runnable command");
                runCommand(c, args);
            }
        }
    }

    //TODO: scan all classes or a package dynamically
    public void register(Class command) {
        commands.add(command);
    }


    private void runCommand(Class command, String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object cmd = command.getConstructors()[0].newInstance();

        List<String> parameters = new ArrayList<>();
        Map<String, String> options = new HashMap<>(); //TODO: fill this

        for(int i = 0; i < args.length; i++) {
            if(args[i].startsWith("-") && !args[i].contains("="))
                i++; //skip the next item if the current parameter is, for instance, "-i", "--input", but not if it is "--input=asd"
            parameters.add(args[i]);
        }
        parameters.remove(0); //remove the actual command

        Arrays.stream(command.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(Parameter.class))
            .forEach(f -> {
                Parameter parameter = f.getDeclaredAnnotation(Parameter.class);
                if(parameters.size() > parameter.index()) {
                    try {
                        f.setAccessible(true);
                        f.set(cmd, parameters.get(parameter.index()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        ((Runnable)cmd).run();
    }

}
