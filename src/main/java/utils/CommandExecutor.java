package utils;

import interfaces.ShutHookHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 22454
 */
@Slf4j
public abstract class CommandExecutor {
    private final LinkedHashMap<Long, Process> processMap;
    private final Map<Long, ShutHookHandler> shutHookMap;
    private final Runtime runtime;

    public CommandExecutor(Runtime runtime) {
        this.runtime = runtime;
        processMap = new LinkedHashMap<>(8);
        shutHookMap = new HashMap<>(8);
        setShutdownHooks();
    }

    private void setShutdownHooks() {
        runtime.addShutdownHook(new Thread(() -> {
            List<Map.Entry<Long, Process>> processEntryList = processMap.entrySet().stream().toList();

            for (int idx = processEntryList.size() - 1; idx >= 0; idx--) {
                Map.Entry<Long, Process> processEntry = processEntryList.get(idx);
                long pid = processEntry.getKey();
                Process process = processEntry.getValue();
                try {
                    if (this.shutHookMap.containsKey(pid)) {
                        shutHookMap.get(pid).handle(process);
                    }
                    this.kill(pid);
                    log.info("kill pid <{}>", pid);
                } catch (Exception e) {
                    log.warn("failed to kill pid <{}>", pid);
                }
                System.exit(0);
            }
        }));
    }

    public synchronized void addShutdownHook(Process process, ShutHookHandler shutHook) {
        this.shutHookMap.put(process.pid(), shutHook);
    }



    /**
     * get os flag
     *
     * @return os flag
     */
    protected abstract int getOsFlag();


    /**
     * kill process
     *
     * @param pid pid
     * @return cmd
     */
    protected abstract String killCmd(long pid);

    public synchronized void kill(long pid) {
        String killCmd = killCmd(pid);
        exec(killCmd);
        processMap.remove(pid);
    }

    /**
     * exec cmd
     *
     * @param cmd cmd
     */
    public synchronized Process exec(String cmd) {
        try {
            Process process = runtime.exec(cmd);
            processMap.put(process.pid(), process);
            return process;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
