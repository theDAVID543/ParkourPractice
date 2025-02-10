package the.david.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import the.david.handler.DataHandler;
import the.david.impl.ParkourPlayer;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerManager{
	static HashMap<UUID, ParkourPlayer> parkourPlayers = new HashMap<>();

	static void addParkourPlayer(UUID uuid){
		parkourPlayers.put(uuid, new ParkourPlayer(Bukkit.getOfflinePlayer(uuid)));
	}

	public static ParkourPlayer getParkourPlayer(OfflinePlayer player){
		UUID uuid = player.getUniqueId();
		ParkourPlayer parkourPlayer;
		if(parkourPlayers.containsKey(uuid)){
			parkourPlayer = parkourPlayers.get(uuid);
			parkourPlayer.player = player.getPlayer();
		}else{
			parkourPlayer = new ParkourPlayer(player);
			parkourPlayer.player = player.getPlayer();
			parkourPlayers.put(uuid, parkourPlayer);
		}
		return parkourPlayer;
	}

	public static void unsetAllParkourPlayers(){
		parkourPlayers.forEach((uuid, parkourPlayer) -> {
			if(parkourPlayer != null && parkourPlayer.player != null){
				parkourPlayer.removeBossBar();
			}
		});
	}

	public static void loadAllParkourPlayers(){
		Set<String> parkourPlayers = DataHandler.getKeys("ParkourPlayers");
		if(parkourPlayers != null){
			parkourPlayers.forEach(uuid -> {
				addParkourPlayer(UUID.fromString(uuid));
			});
		}
	}

	public static List<ParkourPlayer> getParkourPlayerScores(){
		List<ParkourPlayer> scores = new ArrayList<>(parkourPlayers.values());
		scores.sort(Comparator.comparingInt(ParkourPlayer::getParkourScore).reversed());
		return scores;
	}
}
