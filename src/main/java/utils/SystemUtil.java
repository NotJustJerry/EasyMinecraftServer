package utils;

import org.apache.commons.lang3.StringUtils;
import utils.enums.OsEnum;

import java.util.Properties;

/**
 * @author 22454
 */
public class SystemUtil {
    private static Runtime runtime = Runtime.getRuntime();
    private static Properties props = System.getProperties();
    private static String osName = System.getProperty("os.name");


    private static OsEnum defaultOs = OsEnum.WINDOWS;

    public static int getOSFlag() {
        if (StringUtils.isBlank(osName)) {
            return defaultOs.flag;
        }
        osName = osName.toLowerCase();
        for (OsEnum os : OsEnum.values()) {
            if (os.name.contains(osName)) {
                return os.flag;
            }
        }
        return defaultOs.flag;
    }

    public static boolean isWindows() {
        return getOSFlag() == OsEnum.WINDOWS.flag;
    }

    public static boolean isLinux() {
        return getOSFlag() == OsEnum.LINUX.flag;
    }

    public static boolean isMac() {
        return getOSFlag() == OsEnum.MAC.flag;
    }
}
