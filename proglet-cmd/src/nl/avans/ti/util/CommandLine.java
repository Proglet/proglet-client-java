package nl.avans.ti.util;

import nl.avans.ti.commands.Help;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandLine {
    private List<Class> commands = new ArrayList<>();


    public void execute(String[] args) throws Exception {
        String command = null;
        for(int i = 0; i < args.length; i++) {
            if(args[i].startsWith("-") && !args[i].contains("=")) {
                i++; //skip the next item if the current parameter is, for instance, "-i", "--input", but not if it is "--input=asd"
                continue;
            }
            command = args[i];
            break;
        }
        if(command == null) {
            System.err.println("Error parsing command line, no command found");
            return;
        }


        for(Class c : commands) {
            if(c.getDeclaredAnnotation(Command.class) == null)
                continue;
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

        //parse commandline
        List<String> parameters = new ArrayList<>();
        Map<String, String> options = new HashMap<>(); //TODO: fill this
        for(int i = 0; i < args.length; i++) {
            if(args[i].startsWith("-")) {
                String o = args[i];
                if(o.startsWith("--"))
                    o = o.substring(2);
                else if(o.startsWith("-"))
                    o = o.substring(1);

                if(o.contains("="))
                    options.put(o.substring(0, o.indexOf("=")), o.substring(o.indexOf("=")+1)); //TODO: remove quotes
                else {
                    options.put(o, args[i+1]);
                    i++;
                }
            }
            else
                parameters.add(args[i]);
        }
        parameters.remove(0); //remove the actual command

        //set options
        for(Class c : commands) {
            if(c.getDeclaredAnnotation(Command.class) == null) {
                for(Field f : c.getDeclaredFields()) {
                    if(f.isAnnotationPresent(Option.class)) {
                        Option annotation = f.getAnnotation(Option.class);
                        if(options.containsKey(annotation.name())) {
                            String value = options.get(annotation.name());
                            f.setAccessible(true);
                            f.set(null, value);
                        }
                    }
                }

                for(Method m : c.getDeclaredMethods()) {
                    if(m.isAnnotationPresent(Option.class)) {
                        Option annotation = m.getAnnotation(Option.class);
                        if(options.containsKey(annotation.name())) {
                            String value = options.get(annotation.name());
                            m.setAccessible(true);
                            m.invoke(null, value);
                        }
                    }
                }


            }
        }

        //set parameters
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
        //run
        ((Runnable)cmd).run();
    }

}
