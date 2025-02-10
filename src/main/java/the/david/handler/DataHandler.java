package the.david.handler;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import the.david.Main;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class DataHandler{
	static File parkourSetupDataFile;
	static FileConfiguration parkourSetupDataConfig;

	public static void createCustomConfig(){
		parkourSetupDataFile = new File(Main.instance.getDataFolder(), "ParkourData.yml");
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
		try{
			parkourSetupDataConfig.save(parkourSetupDataFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void setObject(String path, Object object){
		parkourSetupDataConfig.set(path, object);
		saveDataFile();
	}

	public static void setString(String path, String value){
		parkourSetupDataConfig.set(path, value);
		saveDataFile();
	}

	public static void setLocation(String path, Location location){
		parkourSetupDataConfig.set(path, location);
		saveDataFile();
	}

	public static void setIntegerList(String path, List<Integer> list){
		parkourSetupDataConfig.set(path, list);
		saveDataFile();
	}

	public static void setInt(String path, int value){
		parkourSetupDataConfig.set(path, value);
		saveDataFile();
	}

	public static void setBoolean(String path, Boolean value){
		parkourSetupDataConfig.set(path, value);
		saveDataFile();
	}

	@Nullable
	public static Boolean getBoolean(String path){
		if(parkourSetupDataConfig.get(path) instanceof Boolean){
			return (Boolean) parkourSetupDataConfig.get(path);
		}
		return null;
	}

	public static Integer getInt(String path){
		return parkourSetupDataConfig.getInt(path);
	}

	public static String getString(String path){
		return parkourSetupDataConfig.getString(path);
	}

	public static Set<String> getKeys(String path){
		if(parkourSetupDataConfig.getConfigurationSection(path) == null){
			return null;
		}else{
			return parkourSetupDataConfig.getConfigurationSection(path).getKeys(false);
		}
	}

	public static Location getLocation(String path){
		return parkourSetupDataConfig.getLocation(path);
	}

	public static List<Integer> getIntegerList(String path){
		return parkourSetupDataConfig.getIntegerList(path);
	}
}
