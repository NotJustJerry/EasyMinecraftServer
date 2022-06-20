package handler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;

/**
 * @author 22454
 */
@Slf4j
public class MinecraftOfflineReminder extends Thread {
    private final MinecraftMessagePublisher publisher;

    private long startTimestamp = 0;
    private long endTimestamp = 0;
    private boolean reminded = true;

    public MinecraftOfflineReminder(MinecraftMessagePublisher publisher) {
        this.publisher = publisher;
        refreshTimestamps();
    }

    private void refreshTimestamps() {
        if (!reminded) {
            return;
        }
        this.reminded = false;
        if (Objects.isNull(Config.getStartTime()) || Objects.isNull(Config.getEndTime())) {
            return;
        }
        Date now = new Date(System.currentTimeMillis());
        String today = DateUtil.format(now, "yyyy-MM-dd");
        String startTime = "%s %s".formatted(today, Config.getStartTime());
        String endTime = "%s %s".formatted(today, Config.getEndTime());
        DateTime startDateTime = DateUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss");
        DateTime endDateTime = DateUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss");
        this.startTimestamp = startDateTime.getTime();
        this.endTimestamp = endDateTime.getTime();
        if (endTimestamp < startTimestamp) {
            this.endTimestamp += 24 * 60 * 60 * 1000L;
            endDateTime = new DateTime(this.endTimestamp);
            endTime = DateUtil.formatDateTime(endDateTime);
        }
        log.info("refresh online range, from %s => %s".formatted(startTime, endTime));
    }

    @Override
    public void run() {
        if (startTimestamp == endTimestamp) {
            return;
        }
        while (publisher.finish()) {
            long currentTimestamp = System.currentTimeMillis();
            if (currentTimestamp == this.endTimestamp - 5 * 60 * 1000 && !this.reminded) {
                this.reminded = true;
                long timeMillis = System.currentTimeMillis();
                Date now = new Date(timeMillis);
                publisher.publish("Current Time: %s,please pay more attention to rest, the server will be shut down in five minutes"
                        .formatted(DateUtil.format(now, "yyyy-MM-dd HH:mm:ss")));
            }
            if (currentTimestamp == this.endTimestamp) {
                this.refreshTimestamps();
            }
        }

    }
}
