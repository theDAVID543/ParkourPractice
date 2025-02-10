package the.david.command.setup;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourLocation;
import the.david.manager.ParkourLocationManager;
import the.david.util.NumberUtils;

import java.util.Map;

public class SetupScore implements SubCommand{
	@Override
	public void execute(Player player, Map<String, String> parsedArgs){
		Integer parkourID = NumberUtils.parseInt(parsedArgs.get("parkourID"));
		if(parkourID == null){
			player.sendMessage(Component.text("跑酷ID格式錯誤", NamedTextColor.RED));
			return;
		}
		ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourID);
		if(parkourLocation == null){
			player.sendMessage(
					Component.text("該跑酷ID不存在").color(NamedTextColor.RED)
			);
			return;
		}
		Integer score = NumberUtils.parseInt(parsedArgs.get("score"));
		if(score == null){
			player.sendMessage(Component.text("分數格式錯誤", NamedTextColor.RED));
			return;
		}
		parkourLocation.setScore(score);
	}
}
