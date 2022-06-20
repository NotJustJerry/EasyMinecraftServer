package config;

import reader.JsonConfigConfigOperator;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * @author 22454
 */
public class Config {
    private final Map<String, Object> configs;
    private final JsonConfigConfigOperator jsonConfigConfigOperator;
    private static final Config instance = new Config();

    private Config() {
        String configCompletePath = System.getProperty("user.dir") +
                File.separator +
                "config" +
                File.separator +
                "config.json";
        jsonConfigConfigOperator = new JsonConfigConfigOperator(configCompletePath);
        configs = jsonConfigConfigOperator.read();
    }

    public static String getStartTime() {
        return (String) getValue("start", "00:00:00");
    }

    public static String getEndTime() {
        return (String) getValue("end", "00:00:00");
    }

    public static Integer getXmx() {
        return (Integer) getValue("xmx", 1024);
    }

    public static Integer getXms() {
        return (Integer) getValue("xms", 1024);
    }

    private static Object getValue(String key, Object defaultValue) {
        try {
            Object value = instance.configs.get(key);
            return Objects.isNull(value) ? defaultValue : value;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

}
