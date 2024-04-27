package the.david.impl;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import the.david.handler.DataHandler;
import the.david.handler.DebugOutputHandler;
import the.david.manager.ParkourLocationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParkourLocation {
    public ParkourLocation(@NotNull Location chooseMethodLocation, int parkourID) {
        this.chooseMethodLocation = chooseMethodLocation;
        this.parkourID = parkourID;
        configPath = "parkourLocations." + parkourID;
        loadData();
    }
    public int parkourID;
    String configPath;
    Location chooseMethodLocation;
    Map<Integer, Location> subParkourLocations = new HashMap<>();
    Map<Integer, Location> parkourPressureLocations = new HashMap<>();
    Location finishLocation;

    void loadData(){
        Set<String> pressureIDs = DataHandler.getKeys(configPath + ".Pressure");
        if (pressureIDs != null) {
            pressureIDs.forEach(pressureID -> {
                Location pressureLocation = DataHandler.getLocation(configPath + ".Pressure." + pressureID);
                parkourPressureLocations.put(Integer.valueOf(pressureID), pressureLocation);
                ParkourLocationManager.addPressureParkourLocation(pressureLocation, this);
                DebugOutputHandler.sendDebugOutput("pressureID " + Integer.valueOf(pressureID) + " : " + pressureLocation);
            });
        }
        Set<String> subParkours = DataHandler.getKeys(configPath + ".Parkour");
        if(subParkours != null){
            subParkours.forEach(subParkour -> {
                Location subParkourLocation = DataHandler.getLocation(configPath + ".Parkour." + subParkour);
                subParkourLocations.put(Integer.valueOf(subParkour), subParkourLocation);
                DebugOutputHandler.sendDebugOutput("SubParkour " + Integer.valueOf(subParkour) + " :" + subParkourLocation);
            });
        }
    }

    public Location getChooseMethodLocation(){
        return chooseMethodLocation;
    }
    public void setFinishLocation(Location finishLocation){
        this.finishLocation = finishLocation;
    }
    public Location getParkourPressureLocation(int subParkourID){
        return parkourPressureLocations.get(subParkourID);
    }
    public Location getSubParkourLocation(int subParkourID){
        return subParkourLocations.get(subParkourID);
    }

    public void addSubParkourLocation(int parkourSubID, Location location){
        subParkourLocations.put(parkourSubID, location);
        DataHandler.setLocation(configPath + ".Parkour." + parkourSubID, location);
    }
    public void addParkourPressureLocation(int parkourSubID, Location location){
        parkourPressureLocations.put(parkourSubID, location);
        DataHandler.setLocation(configPath + ".Pressure." + parkourSubID, location);
        ParkourLocationManager.addPressureParkourLocation(location, this);
    }

    public int getSteppedPressureSubParkourID(Location pressureLocation){
        int subParkourID;
        if(parkourPressureLocations == null){
            return 0;
        }
        for(Map.Entry<Integer, Location> entry : parkourPressureLocations.entrySet()){
            if(entry.getValue().equals(pressureLocation)){
                subParkourID = entry.getKey();
                return subParkourID;
            }
        }
        return 0;
    }
    public Location getParkourLocation(int parkourID){
        return subParkourLocations.get(parkourID);
    }
    public Location getFinishLocation(){
        return finishLocation;
    }
}
