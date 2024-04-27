package the.david.manager;

import org.bukkit.entity.Player;
import the.david.impl.ParkourPlayer;

import java.util.HashMap;

public class PlayerManager {
    static HashMap<Player, ParkourPlayer> parkourPlayers = new HashMap<>();
    public static ParkourPlayer getParkourPlayer(Player player){
        if(!parkourPlayers.containsKey(player)){
            parkourPlayers.put(player, new ParkourPlayer(player));
        }
        return parkourPlayers.get(player);
    }
    public static void unsetAllParkourPlayers(){
        parkourPlayers.forEach((player, parkourPlayer) ->{
            if(parkourPlayer != null){
                parkourPlayer.leavePractice();
            }
        });
    }
}
