package the.david.handler;

import static the.david.Main.plugin;

public class DebugOutputHandler {
    boolean debug = true;
    public static void sendDebugOutput(String message){
        plugin.getLogger().info(message);
    }
}
