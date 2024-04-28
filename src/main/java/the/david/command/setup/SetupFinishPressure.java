package the.david.command.setup;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourLocation;
import the.david.manager.ParkourLocationManager;
import the.david.util.LocationUtils;

import java.util.Map;

public class SetupFinishPressure implements SubCommand {
    @Override
    public void execute(Player player, Map<String, String> parsedArgs) {
        int parkourID;
        try {
            parkourID = Integer.parseInt(parsedArgs.get("parkourID"));
        }catch(NumberFormatException e){
            player.sendMessage(
                    Component.text("該跑酷ID不存在").color(NamedTextColor.RED)
            );
            return;
        }
        Location location = player.getLocation().toBlockLocation();
        location.setYaw(0);
        location.setPitch(0);
        ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourID);
        parkourLocation.setFinishLocation(location.toBlockLocation());
        player.sendMessage(
                Component.text("已設置跑酷結束壓力版位置" + LocationUtils.getLocationString(location)).color(NamedTextColor.GREEN)
        );
    }
}
