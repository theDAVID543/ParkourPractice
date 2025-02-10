package the.david.command.playerManage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourPlayer;
import the.david.manager.PlayerManager;

import java.util.Map;

public class ResetParkourPlayer implements SubCommand{
	@Override
	public void execute(Player player, Map<String, String> parsedArgs){
		if(parsedArgs.containsKey("player")){
			String resetPlayer = parsedArgs.get("player");
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resetPlayer);
			if(offlinePlayer.hasPlayedBefore()){
				ParkourPlayer parkourPlayer = PlayerManager.getParkourPlayer(offlinePlayer);
				parkourPlayer.resetAll();
				player.sendMessage("Succeed reset the player");
			}else{
				player.sendMessage("The player isn't exist!");
			}
		}else{
			ParkourPlayer parkourPlayer = PlayerManager.getParkourPlayer(player);
			parkourPlayer.resetAll();
		}
	}
}
