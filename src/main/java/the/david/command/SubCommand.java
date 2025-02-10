package the.david.command;

import org.bukkit.entity.Player;

import java.util.Map;

public interface SubCommand{
	default Boolean opOnly(){
		return true;
	}

	;

	void execute(Player player, Map<String, String> parsedArgs);
}
