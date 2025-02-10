package the.david.command.setup;

import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourLocation;
import the.david.manager.ParkourLocationManager;

import java.util.Map;

public class SetupCanUsePractice implements SubCommand{
	@Override
	public void execute(Player player, Map<String, String> parsedArgs){
		int parkourID = Integer.parseInt(parsedArgs.get("parkourID"));
		boolean canUsePractice = Boolean.parseBoolean(parsedArgs.get("practicable"));
		ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourID);
		parkourLocation.setCanUsePractice(canUsePractice);
	}
}
