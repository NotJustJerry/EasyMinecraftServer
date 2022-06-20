package handler;

import cn.hutool.core.date.DateUtil;
import config.Config;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * @author 22454
 */
@Slf4j
public class MineCraftServerRunner extends Thread {
    private int xmx;
    private int xms;
    private static final String CMD_PATTERN = "java -Xmx%sM -Xms%sM -jar %s nogui";
    private String runCommand;

    private final Runtime runtime;
    private final String jarPath;

    private volatile Process minecraftProcess;
    private volatile boolean finished;

    public MineCraftServerRunner(String jarPath) {
        this.minecraftProcess = null;
        this.runtime = Runtime.getRuntime();
        this.jarPath = jarPath;
        this.setRunCommand();
        this.finished = false;
        this.setPriority(Thread.MAX_PRIORITY);
    }

    private void setRunCommand() {
        this.xmx = Config.getXmx();
        this.xms = Config.getXms();
        this.runCommand = CMD_PATTERN.formatted(xmx, xms, jarPath);
    }


    @Override
    public void run() {
        log.info("exec {}", this.runCommand);
        try {
            // 启动 server.jar
            minecraftProcess = runtime.exec(this.runCommand);
            BufferedReader minecraftLogReader = new BufferedReader(
                    new InputStreamReader(minecraftProcess.getInputStream(), StandardCharsets.UTF_8)
            );
            while (minecraftProcess.isAlive() && !finished) {
                String lineLog = minecraftLogReader.readLine();
                log.info(lineLog);
            }
        } catch (Exception e) {
            if (Objects.nonNull(minecraftProcess)) {
                BufferedReader minecraftErrorLogReader = new BufferedReader(
                        new InputStreamReader(minecraftProcess.getErrorStream(), StandardCharsets.UTF_8)
                );
                log.error(getLinesFromBuffer(minecraftErrorLogReader));
            }
            log.error("run failed,cause ", e);
        } finally {
            if (Objects.nonNull(minecraftProcess)) {
                log.info("destroy minecraft process");
                minecraftProcess.destroy();
                long pid = minecraftProcess.pid();
                try {
                    runtime.exec("kill -9 %s".formatted(pid));
                } catch (Exception ignored) {
                }
            }
        }
    }

    private String getLinesFromBuffer(BufferedReader reader) {
        Stream<String> lines = reader.lines();
        StringBuilder logBuilder = new StringBuilder();
        lines.forEach(logLine -> logBuilder.append(logBuilder).append("\n"));
        return logBuilder.toString();
    }

    public synchronized void cmd(String cmd) {
        if (Objects.isNull(minecraftProcess)) {
            log.warn("process is null , failed to publish cmd");
            return;
        }
        switch (cmd) {
            case "exit":
                minecraftProcess.onExit().thenApply(v -> {
                    this.setPriority(1);
                    this.finished = true;
                    this.interrupt();
                    log.info("minecraft server exit.");
                    return null;
                });
                this.minecraftProcess.destroy();

                return;
            case "":
                return;
            default:
        }
        BufferedWriter minecraftCommandWriter;
        try {
            minecraftCommandWriter = new BufferedWriter(
                    new OutputStreamWriter(minecraftProcess.getOutputStream(), StandardCharsets.UTF_8)
            );
            minecraftCommandWriter.write("%s\n".formatted(cmd));
            minecraftCommandWriter.flush();
        } catch (Exception e) {
            log.error("命令推送失败，因为：", e);
        }
    }

    public void say(String msg) {
        cmd("/say %s".formatted(msg));
    }

    public synchronized boolean finish() {
        return this.finished;
    }

}
