package nl.avans.ti;

import nl.avans.ti.util.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Settings {
    @Option(name = "host")
    public static void setHost(String hostname)
    {
        Proglet.host = hostname;
    }

    @Option(name = "tokenpath")
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


}
