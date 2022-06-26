package utils.enums;

/**
 * @author 22454
 */

public enum OsEnum {
    WINDOWS("windows", 0),
    LINUX("linux", 1),
    MAC("mac", 2);
    public final String name;
    public final Integer flag;

    OsEnum(String name, Integer flag) {
        this.name = name;
        this.flag = flag;
    }
}

