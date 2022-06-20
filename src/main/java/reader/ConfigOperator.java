package reader;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 22454
 */
public abstract class ConfigOperator {
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected final String configPath;

    protected ConfigOperator(String configPath) {
        this.configPath = configPath;
    }

    public String readBytes() {
        StringBuilder config = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(configPath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                config.append(new String(buffer, 0, length));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config.toString();
    }

    public String[] readLines() {
        List<String> configLines = FileUtil.readLines(configPath, StandardCharsets.UTF_8);
        return configLines.toArray(new String[0]);
    }

    /**
     * 读取配置
     *
     * @return 配置
     */
    public abstract Map<String, Object> read();

    public abstract void write(String key, Object value);
}
