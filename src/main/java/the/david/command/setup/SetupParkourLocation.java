package the.david.command.setup;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourLocation;
import the.david.manager.ParkourLocationManager;
import the.david.util.LocationUtils;

import java.util.Map;

public class SetupParkourLocation implements SubCommand {
    @Override
    public void execute(Player player, Map<String, String> parsedArgs) {
        int parkourID = Integer.parseInt(parsedArgs.get("parkourID"));
        int parkourSubID = Integer.parseInt(parsedArgs.get("SubParkourID"));
        ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourID);
        parkourLocation.addSubParkourLocation(parkourSubID, player.getLocation());
        player.sendMessage(
                Component.text("已設置跑酷位置" + LocationUtils.getLocationString(player.getLocation())).color(NamedTextColor.GREEN)
        );
    }
}
