package the.david.command.info;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourPlayer;
import the.david.manager.PlayerManager;

import java.util.Map;
import java.util.Set;

public class ParkourInfo implements SubCommand {

    @Override
    public void execute(Player player, Map<String, String> parsedArgs) {
        ParkourPlayer parkourPlayer = PlayerManager.getParkourPlayer(player);
        Component component = Component.text().append(Component.text("已完成的跑酷:").color(NamedTextColor.GREEN)).build();
        Set<Integer> finishedParkour = parkourPlayer.getFinishedParkourIDs();
        if(finishedParkour != null){
            for(Integer parkourID : finishedParkour){
                component = component.append(Component.text(" " + parkourID + ",").color(TextColor.color(166,240,163)));
            }
        }
        player.sendMessage(component);
    }
}
