package the.david.command.teleport;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourLocation;
import the.david.manager.ParkourLocationManager;
import the.david.util.NumberUtils;

import java.util.Map;
import java.util.Objects;

public class TeleportParkour implements SubCommand {
    @Override
    public void execute(Player player, Map<String, String> parsedArgs) {
        Integer parkourID = NumberUtils.parseInt(parsedArgs.get("parkourID"));
        if(parkourID == null){
            player.sendMessage(Component.text("跑酷ID格式錯誤", NamedTextColor.RED));
        }
        ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourID);
        if(parkourLocation == null) {
            player.sendMessage(
                    Component.text("該跑酷ID不存在").color(NamedTextColor.RED)
            );
            return;
        }
        String subParkourIDString = parsedArgs.get("SubParkourID");
        if(subParkourIDString != null){
            Integer subParkourID = NumberUtils.parseInt(subParkourIDString);
            if(subParkourID == null){
                player.sendMessage(Component.text("子跑酷ID格式錯誤", NamedTextColor.RED));
                return;
            }
            Location location = parkourLocation.getSubParkourLocation(subParkourID);
            if(location != null){
                player.teleport(location);
            }else{
                player.sendMessage(
                        Component.text("該子跑酷ID不存在").color(NamedTextColor.RED)
                );
            }
            return;
        }
        player.teleport(ParkourLocationManager.getParkourLocation(parkourID).getChooseMethodLocation());
    }
}
