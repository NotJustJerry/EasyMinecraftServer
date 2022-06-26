package utils;

import utils.enums.OsEnum;
import utils.executor.LinuxCommandExecutor;
import utils.executor.WindowsCommandExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 22454
 */
public class SystemCommandExecutorFactory {
    public static final Map<Integer, CommandExecutor> COMMAND_EXECUTOR_MAP = new HashMap<>() {
        {
            put(OsEnum.WINDOWS.flag, new WindowsCommandExecutor());
            put(OsEnum.LINUX.flag, new LinuxCommandExecutor());
        }
    };

    public CommandExecutor factory() {
        int osFlag = SystemUtil.getOSFlag();
        return COMMAND_EXECUTOR_MAP.get(osFlag);
    }
}
