package utils.executor;

import utils.CommandExecutor;
import utils.enums.OsEnum;

/**
 * @author 22454
 */
public class LinuxCommandExecutor extends CommandExecutor {
    public LinuxCommandExecutor() {
        super(Runtime.getRuntime());
    }

    @Override
    public int getOsFlag() {
        return OsEnum.LINUX.flag;
    }


    @Override
    protected String killCmd(long pid) {
        return "kill -9 %s".formatted(pid);
    }
}
