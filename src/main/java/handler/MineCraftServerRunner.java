package handler;

import config.Config;
import interfaces.ShutHookHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import utils.CommandExecutor;
import utils.SystemCommandExecutorFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author 22454
 */
@Slf4j
public class MineCraftServerRunner extends Thread {
    private static final SystemCommandExecutorFactory COMMAND_EXECUTOR_FACTORY = new SystemCommandExecutorFactory();
    private final CommandExecutor commandExecutor = COMMAND_EXECUTOR_FACTORY.factory();
    private int xmx;
    private int xms;
    private static final String CMD_PATTERN = "java -Xmx%sM -Xms%sM -jar %s nogui";
    private final String runCommand;

    private volatile Process minecraftProcess;
    private BufferedReader minecraftInfoLogReader;
    private BufferedReader minecraftErrorLogReader;
    private BufferedWriter minecraftCommandWriter;
    private volatile boolean finished;
    private volatile boolean threadStarted;

    public MineCraftServerRunner(String jarPath) {
        this.minecraftProcess = null;
        this.readConfig();
        this.runCommand = CMD_PATTERN.formatted(xmx, xms, jarPath);
        this.finished = false;
        this.threadStarted = false;
        this.setPriority(Thread.MAX_PRIORITY);
    }

    private void readConfig() {
        this.xmx = Config.getXmx();
        this.xms = Config.getXms();
    }


    private void createMinecraftProcess() {
        // 启动 server.jar
        minecraftProcess = commandExecutor.exec(this.runCommand);
        log.info("Minecraft process PID : [ {} ]", minecraftProcess.pid());
        log.info(minecraftProcess.info().toString());
        // info log
        this.minecraftInfoLogReader = new BufferedReader(
                new InputStreamReader(minecraftProcess.getInputStream(), StandardCharsets.UTF_8)
        );
        this.minecraftErrorLogReader = new BufferedReader(
                new InputStreamReader(minecraftProcess.getErrorStream(), StandardCharsets.UTF_8)
        );
        this.minecraftCommandWriter = new BufferedWriter(
                new OutputStreamWriter(minecraftProcess.getOutputStream(), StandardCharsets.UTF_8)
        );
        // set exit event handler
        minecraftProcess.onExit().thenRun(() -> {
            minecraftProcess = null;
            MineCraftServerRunner.this.minecraftProcess = null;
            MineCraftServerRunner.this.minecraftInfoLogReader = null;
            MineCraftServerRunner.this.minecraftErrorLogReader = null;
            MineCraftServerRunner.this.minecraftCommandWriter = null;
        });
        commandExecutor.addShutdownHook(minecraftProcess, new ShutHookHandler() {
            @Override
            public <T> T handle(Process process) {
                if (minecraftProcess.isAlive()) {
                    MineCraftServerRunner.this.cmd("exit");
                }
                log.info("minecraft server exit.");
                return null;
            }
        });
    }

    @Override
    public synchronized void start() {
        super.start();
        this.threadStarted = true;
    }

    @Override
    public void run() {
        log.info("exec {}", this.runCommand);
        try {
            while (!finished) {
                if (Objects.nonNull(minecraftInfoLogReader)) {
                    String lineLog = minecraftInfoLogReader.readLine();
                    if (StringUtils.isNotBlank(lineLog)) {
                        System.out.println(lineLog);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Objects.nonNull(minecraftProcess)) {
                log.error(getLinesFromBuffer(minecraftErrorLogReader));
            }
            log.error("run failed,cause ", e);
        } finally {
            if (Objects.nonNull(minecraftProcess)) {
                log.info("destroy minecraft process");
                minecraftProcess.destroy();
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
        cmd = toMinecraftCommand(cmd);
        if (Objects.isNull(minecraftProcess)) {
            log.warn("process is null , failed to publish cmd");
            return;
        }
        if (StringUtils.isBlank(cmd)) {
            return;
        }
        exec(cmd);
    }

    private void exec(String cmd) {
        try {
            this.minecraftCommandWriter.write("%s\n".formatted(cmd));
            this.minecraftCommandWriter.flush();
        } catch (Exception e) {
            log.error("failed to push command to minecraft server,Cause : ", e);
        }
    }

    public synchronized void terminate() {
        this.finished = true;
        cmd("exit");
    }


    public synchronized boolean finish() {
        return this.finished;
    }

    private String toMinecraftCommand(String cmd) {
        switch (cmd) {
            case "exit", "stop" -> {
                if (Objects.isNull(minecraftProcess)) {
                    log.error("minecraft process stopped");
                    return "";
                }
                return "/stop";
            }
            case "start" -> {
                if (Objects.isNull(minecraftProcess)) {
                    this.createMinecraftProcess();
                } else {
                    log.error("minecraft process running..");
                }
                if (!threadStarted) {
                    this.start();
                }
                return "";
            }
            default -> {
            }
        }
        return cmd;
    }
}
