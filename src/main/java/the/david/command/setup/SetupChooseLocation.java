package the.david.command.setup;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.manager.ParkourLocationManager;
import the.david.util.LocationUtils;

import java.util.Map;
import java.util.Objects;

public class SetupChooseLocation implements SubCommand {
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
        Location location = player.getLocation();
        if(Objects.equals(parsedArgs.get("toCenter"), "true")){
            location = LocationUtils.toParkourCenter(location, true);
        }
        ParkourLocationManager.addParkourLocation(parkourID, location);
        player.sendMessage(
                Component.text("已設置跑酷選擇區位置" + LocationUtils.getLocationString(location)).color(NamedTextColor.GREEN)
        );
    }
}
