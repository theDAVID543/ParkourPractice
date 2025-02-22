package the.david.command.playerManage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourPlayer;
import the.david.manager.ParkourLocationManager;
import the.david.manager.PlayerManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParkourPlayerInfo implements SubCommand{
	@Override
	public Boolean opOnly(){
		return false;
	}

	@Override
	public void execute(Player player, Map<String, String> parsedArgs){
		ParkourPlayer parkourPlayer = PlayerManager.getParkourPlayer(player);
		Component component = Component.text().append(Component.text("已完成的跑酷:").color(NamedTextColor.GREEN)).build();
		Set<Integer> finishedParkour = parkourPlayer.getFinishedParkourIDs();
		if(finishedParkour != null){
			List<Integer> finishedParkourList = new java.util.ArrayList<>(finishedParkour.stream().toList());
			finishedParkourList.sort(Integer::compareTo);
			for(Integer parkourID : finishedParkourList){
				component = component.append(Component.text(" " + parkourID + ",").color(ParkourLocationManager.getParkourIdColor(parkourID)));
			}
		}
		component = component.appendNewline().append(Component.text("跑酷得分: " + parkourPlayer.getParkourScore()).color(TextColor.color(242, 255, 61)));
		player.sendMessage(component);
	}
}
