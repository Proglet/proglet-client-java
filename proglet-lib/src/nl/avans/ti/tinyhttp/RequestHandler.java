package nl.avans.ti.tinyhttp;

import java.util.Map;

public interface RequestHandler {
    String onRequest(Map<String, String> parameters);
}
