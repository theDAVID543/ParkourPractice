package the.david.command;

import org.bukkit.entity.Player;

import java.util.Map;

public interface SubCommand {
	void execute(Player player, Map<String, String> parsedArgs);
}
