package nl.avans.ti;

import nl.avans.ti.util.Option;

public class Settings {
    @Option(name = "host")
    public static void setHost(String hostname)
    {
        Proglet.host = hostname;
    }
}
