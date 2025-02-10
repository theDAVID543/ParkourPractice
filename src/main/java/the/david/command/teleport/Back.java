package the.david.command.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourLocation;
import the.david.impl.ParkourPlayer;
import the.david.manager.ParkourLocationManager;
import the.david.manager.PlayerManager;

import java.util.Map;

public class Back implements SubCommand{
	@Override
	public Boolean opOnly(){
		return false;
	}

	@Override
	public void execute(Player player, Map<String, String> parsedArgs){
		ParkourPlayer parkourPlayer = PlayerManager.getParkourPlayer(player);
		ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourPlayer.playingParkourID);
		if(parkourLocation == null){
			player.sendMessage(
					Component.text("此跑酷ID不存在").color(NamedTextColor.RED)
			);
			return;
		}
		player.teleport(parkourLocation.getChooseMethodLocation());
	}
}
