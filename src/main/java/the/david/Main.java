package the.david;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import the.david.command.CommandManager;
import the.david.command.TabCompleteManager;
import the.david.handler.DataHandler;
import the.david.handler.EventHandler;
import the.david.manager.ParkourLocationManager;
import the.david.manager.PlayerManager;

import static the.david.manager.PlayerManager.unsetAllParkourPlayers;

public final class Main extends JavaPlugin{
	public EventHandler eventHandler;
	public CommandManager commandManager;
	public TabCompleteManager tabCompleteManager;
	public static JavaPlugin instance;
	public static Main plugin;
	public static LuckPerms luckPerms;

	@Override
	public void onEnable(){
		luckPerms = getServer().getServicesManager().load(LuckPerms.class);
		plugin = this;
		instance = this;
		eventHandler = new EventHandler();
		getServer().getPluginManager().registerEvents(eventHandler, this);
		commandManager = new CommandManager();
		Bukkit.getPluginCommand("parkourpractice").setExecutor(commandManager);
		tabCompleteManager = new TabCompleteManager();
		Bukkit.getPluginCommand("parkourpractice").setTabCompleter(tabCompleteManager);
		DataHandler.createCustomConfig();
		ParkourLocationManager.loadParkourLocations();
		PlayerManager.loadAllParkourPlayers();
	}

	@Override
	public void onDisable(){
		// Plugin shutdown logic
		unsetAllParkourPlayers();
	}
}
