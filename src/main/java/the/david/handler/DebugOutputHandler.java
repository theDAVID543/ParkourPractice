package the.david.handler;

import static the.david.Main.plugin;

public class DebugOutputHandler{
	final static boolean debug = false;

	public static void sendDebugOutput(String message){
		if(debug){
			plugin.getLogger().info(message);
		}
	}
}
