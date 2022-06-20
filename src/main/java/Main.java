import handler.MineCraftServerRunner;
import handler.MinecraftCommandExecutor;
import handler.MinecraftMessagePublisher;
import handler.MinecraftOfflineReminder;

import java.util.Scanner;

/**
 * @author 22454
 */
public class Main {
    private final MineCraftServerRunner runner;
    private final MinecraftMessagePublisher publisher;
    private final MinecraftOfflineReminder generator;

    private final MinecraftCommandExecutor executor;

    public Main(String[] args) {
        this.runner = new MineCraftServerRunner(args[0]);
        this.publisher = new MinecraftMessagePublisher(runner);
        this.generator = new MinecraftOfflineReminder(publisher);
        this.executor = new MinecraftCommandExecutor(runner);
    }

    public void start() {
        publisher.start();
        generator.start();
        runner.start();
        executor.start();

        Scanner scanner = new Scanner(System.in);
        while (!runner.finish()) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                executor.exec(line);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Main main = new Main(args);
            main.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
