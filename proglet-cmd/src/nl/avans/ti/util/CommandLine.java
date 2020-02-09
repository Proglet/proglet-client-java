package nl.avans.ti.util;

import nl.avans.ti.commands.Help;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandLine {
    private List<Class> commands = new ArrayList<>();

    private List<String> parameters = new ArrayList<>();
    private Map<String, String> options = new HashMap<>();

    public void execute(String[] args) throws Exception {
        if(parameters.size() == 0)
            parameters.add("help");

        for(Class c : commands) {
            if(c.getDeclaredAnnotation(Command.class) == null)
                continue;
            Command commandAnnotation = (Command)c.getDeclaredAnnotation(Command.class); //TODO: should this be casted?

            boolean match = true;
            String[] splitted = commandAnnotation.command().split(" ");
            for(int i = 0; i < splitted.length; i++)
                if(parameters.size() <= i || !splitted[i].equals(parameters.get(i)))
                    match = false;
            if(match) {
                if(!Runnable.class.isAssignableFrom(c))
                    throw new Exception("Command " + c.getName() + " is not a Runnable command");
                runCommand(c, args);
                return;
            }
        }
        System.out.println("Command " + parameters.get(0) + " not found");
    }

    //TODO: scan all classes or a package dynamically
    public void register(Class command) {
        commands.add(command);
    }


    private void runCommand(Class command, String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object cmd = command.getConstructors()[0].newInstance();

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

        //sets commandline if needed
        Arrays.stream(command.getDeclaredFields())
                .filter(f -> f.getType().equals(CommandLine.class))
                .forEach(f -> {
                    f.setAccessible(true);
                    try {
                        f.set(cmd, this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        //run
        ((Runnable)cmd).run();
    }

    public void parseCommandLine(String[] args) {
        parameters = new ArrayList<>();
        options = new HashMap<>();

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
    }

    public void setOptions()
    {
        for(Class c : commands) {
            if(c.getDeclaredAnnotation(Command.class) == null) {
                for(Field f : c.getDeclaredFields()) {
                    if(f.isAnnotationPresent(Option.class)) {
                        Option annotation = f.getAnnotation(Option.class);
                        if(options.containsKey(annotation.name())) {
                            String value = options.get(annotation.name());
                            f.setAccessible(true);
                            try {
                                f.set(null, value);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                for(Method m : c.getDeclaredMethods()) {
                    if(m.isAnnotationPresent(Option.class)) {
                        Option annotation = m.getAnnotation(Option.class);
                        if(options.containsKey(annotation.name())) {
                            String value = options.get(annotation.name());
                            m.setAccessible(true);
                            try {
                                m.invoke(null, value);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    public List<Class> getCommands()
    {
        return this.commands;
    }

    public void registerPackage(String packageName) {
        List<String> classes = ClassScanner.findClassesInPackage(packageName, true);
        classes.stream().forEach(c -> {
            try {
                register(Class.forName(c));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
