package the.david.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import the.david.handler.DataHandler;
import the.david.impl.ParkourPlayer;

import java.util.*;

public class PlayerManager {
    static HashMap<UUID, ParkourPlayer> parkourPlayers = new HashMap<>();
    static void addParkourPlayer(OfflinePlayer player){
        parkourPlayers.put(player.getUniqueId(), new ParkourPlayer(player));
    }
    public static ParkourPlayer getParkourPlayer(OfflinePlayer player){
        UUID uuid = player.getUniqueId();
        if (!parkourPlayers.containsKey(uuid)) {
            parkourPlayers.put(player.getUniqueId(), new ParkourPlayer(player));
        }
        ParkourPlayer parkourPlayer = parkourPlayers.get(uuid);
        if (parkourPlayer.player == null && player.isOnline()) {
            parkourPlayer.player = player.getPlayer();
        }
        return parkourPlayer;
    }
    public static void unsetAllParkourPlayers(){
        parkourPlayers.forEach((uuid, parkourPlayer) ->{
            if(parkourPlayer != null){
                parkourPlayer.leavePractice();
            }
        });
    }
    public static void loadAllParkourPlayers(){
        Set<String> parkourPlayers = DataHandler.getKeys("ParkourPlayers");
        if(parkourPlayers != null) {
            parkourPlayers.forEach(uuid -> {
                addParkourPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
            });
        }
    }
    public static List<ParkourPlayer> getParkourPlayerScores(){
        List<ParkourPlayer> scores = new ArrayList<>(parkourPlayers.values());
        scores.sort(Comparator.comparingInt(ParkourPlayer::getParkourScore).reversed());
        return scores;
    }
}
