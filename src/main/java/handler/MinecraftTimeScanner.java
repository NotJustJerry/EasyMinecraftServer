package handler;

import cn.hutool.core.collection.CollectionUtil;
import interfaces.TimeMonitor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 22454
 */
@Slf4j
public class MinecraftTimeScanner extends Thread {
    private final MineCraftServerRunner runner;
    private List<TimeMonitor> timeMonitorList;

    public MinecraftTimeScanner(MineCraftServerRunner runner) {
        this.runner = runner;
    }

    @Override
    public void run() {
        while (!runner.finish()) {
            long currentTimestamp = System.currentTimeMillis();
            notifyTimeMonitor(currentTimestamp);
        }
    }

    public void notifyTimeMonitor(long currentTimestamp) {
        for (TimeMonitor timeMonitor : timeMonitorList) {
            try {
                timeMonitor.handle(currentTimestamp);
            } catch (Exception e) {
                log.warn("failed to notify task for {},Cause ", timeMonitor.getClass().getName(), e);
            }

        }
    }

    public synchronized void registerTimeMonitor(TimeMonitor monitor) {
        if (CollectionUtil.isEmpty(timeMonitorList)) {
            timeMonitorList = new LinkedList<>();
        }
        timeMonitorList.add(monitor);
    }
}
