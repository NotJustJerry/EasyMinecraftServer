package handler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 22454
 */
@Slf4j
public class MinecraftMessagePublisher extends MinecraftCommandExecutor {

    public MinecraftMessagePublisher(MineCraftServerRunner mineCraftServerRunner) {
        super(mineCraftServerRunner);
    }

    public void publish(String msg) {
        super.exec("/say %s".formatted(msg));
    }
}
