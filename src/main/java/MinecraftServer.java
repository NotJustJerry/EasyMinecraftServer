import handler.*;

/**
 * @author 22454
 */
public class MinecraftServer {
    private final MineCraftServerRunner runner;
    private final MinecraftOnlineController reminder;

    private final MinecraftTimeScanner minecraftTimeScanner;

    private final MinecraftCommandReceiver receiver;
    private final TerminalCommandScanner terminalCommandScanner;

    public MinecraftServer(String[] args) {
        this.runner = new MineCraftServerRunner(args[0]);
        this.terminalCommandScanner = new TerminalCommandScanner();
        this.minecraftTimeScanner = new MinecraftTimeScanner(runner);
        this.receiver = new MinecraftCommandReceiver(runner);
        this.reminder = new MinecraftOnlineController(runner);
        init();
    }

    private void init() {
        terminalCommandScanner.registerTerminalCommandMonitors(receiver);
        minecraftTimeScanner.registerTimeMonitor(reminder);
    }

    public void start() {
        terminalCommandScanner.run();
    }
}
