package handler;

import cn.hutool.core.collection.CollectionUtil;
import interfaces.TerminalCommandMonitor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * @author 22454
 */
@Slf4j
@Setter
@Getter
@ToString
public class TerminalCommandScanner {
    private final Scanner terminalScanner;
    private boolean finished;

    private List<TerminalCommandMonitor> terminalCommandMonitorList;

    public TerminalCommandScanner() {
        this.terminalScanner = new Scanner(System.in);
        this.finished = false;
    }

    public void run() {
        while (!finished) {
            if (!terminalScanner.hasNextLine()) {
                continue;
            }
            String command = terminalScanner.nextLine();
            push(command);
        }
    }

    private void push(String command) {
        for (TerminalCommandMonitor commandMonitor : terminalCommandMonitorList) {
            try {
                commandMonitor.receive(command);
            } catch (Exception e) {
                log.warn("TerminalCommandScanner failed to push cmd <{}> to {}, Cause ", command, commandMonitor, e);
            }
        }
    }

    public synchronized void registerTerminalCommandMonitors(TerminalCommandMonitor... monitors) {
        if (CollectionUtil.isEmpty(terminalCommandMonitorList)) {
            terminalCommandMonitorList = new LinkedList<>();
        }
        this.terminalCommandMonitorList.addAll(Arrays.asList(monitors));
    }
}
