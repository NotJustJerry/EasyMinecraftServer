/**
 * @author 22454
 */
public class Main {

    public static void main(String[] args) {
        try {
            MinecraftServer minecraftServer = new MinecraftServer(args);
            minecraftServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
