package handler;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Objects;

/**
 * @author 22454
 */
@Slf4j
public class MinecraftCommandExecutor extends Thread {
    protected final LinkedList<String> msgQueue;
    protected final MineCraftServerRunner mineCraftServerRunner;

    public MinecraftCommandExecutor(MineCraftServerRunner mineCraftServerRunner) {
        this.msgQueue = new LinkedList<>();
        this.mineCraftServerRunner = mineCraftServerRunner;
    }

    @Override
    public void run() {
        while (Objects.nonNull(mineCraftServerRunner) && !mineCraftServerRunner.finish()) {
            try {
                if (msgQueue.size() > 0) {
                    String firstMsg = msgQueue.pop();
                    mineCraftServerRunner.cmd(firstMsg);
                }
            } catch (Exception e) {
                log.warn("failed to publish cmd,cause ", e);
            }
        }
    }

    public void exec(String cmd) {
        this.msgQueue.push(cmd);
    }

    public boolean finish() {
        return Objects.isNull(mineCraftServerRunner) || mineCraftServerRunner.finish();
    }
}
