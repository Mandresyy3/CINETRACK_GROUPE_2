package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {
    private static final Map<String, String> envMap = new HashMap<>();

    public static void load(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // Skip blanks and comments

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    envMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load .env file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return envMap.getOrDefault(key, System.getenv(key)); // Fallback to system env
    }
}
