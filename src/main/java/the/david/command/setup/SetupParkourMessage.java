package the.david.command.setup;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourLocation;
import the.david.manager.ParkourLocationManager;

import java.util.Map;

public class SetupParkourMessage implements SubCommand {
    @Override
    public void execute(Player player, Map<String, String> parsedArgs) {
        int parkourID = Integer.parseInt(parsedArgs.get("parkourID"));
        int parkourSubID = Integer.parseInt(parsedArgs.get("SubParkourID"));
        ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourID);
        String parkourMessage = parsedArgs.get("message");
        parkourLocation.setParkourMessage(parkourSubID, parkourMessage);
        Component message = MiniMessage.miniMessage().deserialize(parkourMessage);
        player.sendMessage(
                Component.text("已設置跑酷訊息為")
                        .appendNewline().append(message)
        );
    }
}
