package handler;

import interfaces.TerminalCommandMonitor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 22454
 */
@Slf4j
public record MinecraftCommandReceiver(MineCraftServerRunner mineCraftServerRunner) implements TerminalCommandMonitor {
    @Override
    public void receive(String command) {
        mineCraftServerRunner.cmd(command);
    }
}
