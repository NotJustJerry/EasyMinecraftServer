package reader;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 22454
 */
@Slf4j
public class JsonConfigConfigOperator extends ConfigOperator {

    public JsonConfigConfigOperator(String configPath) {
        super(configPath);
    }

    public Map<String, Object> read() {
        String json = readBytes();
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ignored) {
        }
        return new HashMap<>(8);
    }

    @Override
    public void write(String key, Object value) {
        Map<String, Object> config = this.read();
        config.put(key, value);
        String jsonConfig;
        try {
            jsonConfig = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            FileUtil.writeBytes(jsonConfig.getBytes(), configPath);
        } catch (JsonProcessingException e) {
            log.error("failed to write config: %s".formatted(configPath));
        }
    }
}
