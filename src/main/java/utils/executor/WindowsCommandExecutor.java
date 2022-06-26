package utils.executor;

import utils.CommandExecutor;
import utils.enums.OsEnum;

/**
 * @author 22454
 */
public class WindowsCommandExecutor extends CommandExecutor {
    public WindowsCommandExecutor() {
        super(Runtime.getRuntime());
    }

    @Override
    public int getOsFlag() {
        return OsEnum.WINDOWS.flag;
    }


    @Override
    protected String killCmd(long pid) {
        return "taskkill -PID %s -F".formatted(pid);
    }

    public static void main(String[] args) {
        new WindowsCommandExecutor().kill(8364);
    }
}
