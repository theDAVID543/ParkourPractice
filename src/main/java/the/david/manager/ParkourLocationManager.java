package the.david.manager;

import org.bukkit.Location;
import the.david.handler.DataHandler;
import the.david.handler.DebugOutputHandler;
import the.david.impl.ParkourLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParkourLocationManager{
    static Map<Integer, ParkourLocation> parkourLocations = new HashMap<>();
    public static Map<Location, ParkourLocation> parkourPressures = new HashMap<>();
    public static ParkourLocation getParkourLocation(int id){
        return parkourLocations.get(id);
    }
    public static void addParkourLocation(int id, Location chooseMethodLocation){
        parkourLocations.put(id, new ParkourLocation(chooseMethodLocation, id));
        DataHandler.setLocation("parkourLocations." + id + ".ChooseMethodLocation", chooseMethodLocation);
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
}
