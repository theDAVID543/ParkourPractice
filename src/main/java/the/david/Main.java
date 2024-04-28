package the.david;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import the.david.command.CommandManager;
import the.david.command.TabCompleteManager;
import the.david.handler.DataHandler;
import the.david.handler.EventHandler;
import the.david.manager.ParkourLocationManager;

import static the.david.manager.PlayerManager.unsetAllParkourPlayers;

public final class Main extends JavaPlugin {
    public EventHandler eventHandler;
    public CommandManager commandManager;
    public TabCompleteManager tabCompleteManager;
    public static JavaPlugin instance;
    public static Main plugin;

    @Override
    public void onEnable() {
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        unsetAllParkourPlayers();
    }
}
