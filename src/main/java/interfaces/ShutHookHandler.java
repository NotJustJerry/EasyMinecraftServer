package interfaces;

/**
 * @author 22454
 */
public interface ShutHookHandler {
    /**
     * do something before exit main thread
     *
     * @param <T>     res type
     * @param process process
     * @return res
     */
    <T> T handle(Process process);
}
