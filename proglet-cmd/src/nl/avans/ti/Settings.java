package nl.avans.ti;

import nl.avans.ti.util.Option;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

public class Settings {
    @Option(name = "host")
    public static void setHost(String hostname)
    {
        Proglet.host = hostname;
    }

    @Option(name = "settingspath")
    public static String dataFolder = System.getenv("APPDATA") + "/proglet";


    public static String getJwtToken()
    {
        checkDataPath();
        if(!Files.exists(Paths.get(dataFolder + "/token")))
            return "";
        try {
            return Files.readString(Paths.get(dataFolder + "/token"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setJwtToken(String token)
    {
        checkDataPath();
        try {
            Files.writeString(Paths.get(dataFolder + "/token"), token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    private static void checkDataPath() {
        try {
            if(Files.notExists(Paths.get(dataFolder)))
                Files.createDirectory(Paths.get(dataFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void clearDefault(String name) {
        List<String> lines = new ArrayList<String>();
        try {
            lines = Files.readAllLines(Paths.get(dataFolder + "/settings.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean found = false;
        for(int i = 0; i < lines.size(); i+=2) {
            if (lines.get(i).equals(name)) {
                lines.remove(i);
                lines.remove(i);
                break;
            }
        }

        try {
            Files.writeString(Paths.get(dataFolder + "/settings.txt"), String.join("\n", lines));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void storeDefault(String name, String value) {
        List<String> lines = new ArrayList<String>();
        try {
            lines = Files.readAllLines(Paths.get(dataFolder + "/settings.txt"));
        } catch(NoSuchFileException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean found = false;
        for(int i = 0; i < lines.size(); i+=2) {
            if (lines.get(i).equals(name)) {
                lines.set(i + 1, value);
                found = true;
            }
        }

        if(!found) {
            lines.add(name);
            lines.add(value);
        }
        try {
            Files.writeString(Paths.get(dataFolder + "/settings.txt"), String.join("\n", lines));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadDefaults() {
        List<String> lines = new ArrayList<String>();
        try {
            lines = Files.readAllLines(Paths.get(dataFolder + "/settings.txt"));
        } catch(NoSuchFileException e) {
        }catch(IOException e) {
            e.printStackTrace();
        }
        //make lookup of file
        Map<String,String> defaults = new HashMap<>();
        for(int i = 0; i < lines.size(); i+=2)
            defaults.put(lines.get(i), lines.get(i+1));

        //set attributes
        Arrays.stream(Settings.class.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Option.class)).forEach(f -> {
            Option field = f.getAnnotation(Option.class);
            if(defaults.containsKey(field.name())) {
                try {
                    f.set(null, defaults.get(field.name()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        //set methods
        Arrays.stream(Settings.class.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Option.class)).forEach(m -> {
            Option field = m.getAnnotation(Option.class);
            if(defaults.containsKey(field.name())) {
                try {
                   m.invoke(null, defaults.get(field.name()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
