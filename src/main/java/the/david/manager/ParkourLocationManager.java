package the.david.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import the.david.handler.DataHandler;
import the.david.handler.DebugOutputHandler;
import the.david.impl.ParkourDifficulty;
import the.david.impl.ParkourLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParkourLocationManager{
    static Map<Integer, ParkourLocation> parkourLocations = new HashMap<>();
    public static Map<Location, ParkourLocation> parkourPressures = new HashMap<>();
    public static Map<Location, ParkourLocation> finishLocations = new HashMap<>();
    public static ParkourLocation getParkourLocation(int id){
        return parkourLocations.get(id);
    }
    public static void addParkourLocation(int id, Location chooseMethodLocation){
        parkourLocations.put(id, new ParkourLocation(chooseMethodLocation, id));
        DataHandler.setLocation("parkourLocations." + id + ".ChooseMethodLocation", chooseMethodLocation);
    }
    public static void addFinishPressure(ParkourLocation parkourLocation, Location finishPressureLocation){
        finishLocations.put(finishPressureLocation, parkourLocation);
    }
    public static void loadParkourLocations(){
        Set<String> parkourLocations = DataHandler.getKeys("parkourLocations");
        if(parkourLocations != null){
            parkourLocations.forEach(parkourID ->{
                Location chooseMethodLocation = DataHandler.getLocation("parkourLocations." + parkourID + ".ChooseMethodLocation");
                DebugOutputHandler.sendDebugOutput(parkourID);
                DebugOutputHandler.sendDebugOutput(chooseMethodLocation.toString());
                addParkourLocation(Integer.parseInt(parkourID), chooseMethodLocation);
            });
        }
    }
    public static void addPressureParkourLocation(Location pressureLocation, ParkourLocation parkourLocation){
        parkourPressures.put(pressureLocation, parkourLocation);
    }
    final static Map<ParkourDifficulty, TextColor> parkourDifficultyColors = new HashMap<>(){{
        put(ParkourDifficulty.GREEN, TextColor.color(96, 252, 112));
        put(ParkourDifficulty.YELLOW, TextColor.color(242, 242, 24));
        put(ParkourDifficulty.ORANGE, TextColor.color(250, 158, 37));
        put(ParkourDifficulty.RED, TextColor.color(242, 19, 19));
        put(ParkourDifficulty.PURPLE, TextColor.color(132, 24, 240));
        put(ParkourDifficulty.BLACK, TextColor.color(0, 0, 0));
    }};
    public static TextColor getParkourIdColor(int id){
        return parkourDifficultyColors.get(getParkourLocation(id).getDifficulty());
    }
}
