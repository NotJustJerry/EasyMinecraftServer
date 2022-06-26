package reader;

import cn.hutool.core.io.FileUtil;

import java.util.*;

/**
 * @author 22454
 */
public class PropertiesOperator extends ConfigOperator {
    public PropertiesOperator(String configPath) {
        super(configPath);
    }

    @Override
    public Map<String, Object> read() {
        String[] lines = readLines();
        Map<String, Object> config = new HashMap<>(lines.length);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            line = line.trim();
            if (line.length() == 0 || line.charAt(0) == '#') {
                config.put("##useless_%s".formatted(i), line);
                continue;
            }
            String[] keyAndValue = line.split("=");
            while (keyAndValue.length < 2) {
                List<String> kAndV = new ArrayList<>(Arrays.asList(keyAndValue));
                kAndV.add("");
                keyAndValue = kAndV.toArray(new String[0]);
            }
            String key = keyAndValue[0].trim();
            String value = keyAndValue[1].trim();
            config.put(key, value);
        }
        return config;
    }

    @Override
    public void write(String key, Object value) {
        Map<String, Object> config = this.read();
        config.put(key, value);
        StringBuilder configTxt = new StringBuilder();
        config.forEach((configKey, configValue) -> configTxt.append(configKey.startsWith("##useless_") ?
                configTxt.append(configValue).append("\n") :
                configTxt.append("%s = %s\n".formatted(configKey, configValue))));
        FileUtil.writeBytes(configTxt.toString().getBytes(), configPath);
    }
}
