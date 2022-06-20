package config;

import reader.PropertiesOperator;

import java.io.File;
import java.util.Map;

/**
 * @author 22454
 */
public class EulaEditor {
    private final PropertiesOperator propertiesOperator;
    private final Map<String, Object> config;

    public EulaEditor() {
        String configCompletePath = System.getProperty("user.dir") +
                File.separator +
                "config" +
                File.separator +
                "config.json";
        this.propertiesOperator = new PropertiesOperator(configCompletePath);
        config = propertiesOperator.read();
    }

    public void write(String key, Object value) {
        propertiesOperator.write(key, value);
    }
}
