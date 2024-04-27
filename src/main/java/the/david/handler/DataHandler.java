package the.david.handler;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import the.david.Main;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class DataHandler {
    static File parkourSetupDataFile;
    static FileConfiguration parkourSetupDataConfig;
    public static void createCustomConfig(){
        parkourSetupDataFile = new File(Main.instance.getDataFolder(), "ParkourSetupData.yml");
        if(!parkourSetupDataFile.exists()){
            parkourSetupDataFile.getParentFile().mkdirs();
            try{
                parkourSetupDataFile.createNewFile();
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
        parkourSetupDataConfig = new YamlConfiguration();
        try{
            parkourSetupDataConfig.load(parkourSetupDataFile);
        }catch(IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public static void saveDataFile(){
        try {
            parkourSetupDataConfig.save(parkourSetupDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setLocation(String path, Location location){
        parkourSetupDataConfig.set(path, location);
        saveDataFile();
    }
    public static Set<String> getKeys(String path){
        if(parkourSetupDataConfig.getConfigurationSection(path) == null){
            return null;
        }else {
            return parkourSetupDataConfig.getConfigurationSection(path).getKeys(false);
        }
    }
    public static Location getLocation(String path){
        return parkourSetupDataConfig.getLocation(path);
    }
}
