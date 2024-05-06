package the.david.command.list;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourPlayer;
import the.david.manager.PlayerManager;

import java.util.List;
import java.util.Map;

public class ListParkourScore implements SubCommand {
    @Override
    public void execute(Player player, Map<String, String> parsedArgs) {
        Component component = Component.text().append(Component.text("---------跑酷得分排行榜---------").color(TextColor.color(55, 218, 250))).build();
        List<ParkourPlayer> parkourPlayers = PlayerManager.getParkourPlayerScores();
        for (ParkourPlayer parkourPlayer : parkourPlayers) {
            String scoreString = "  " + parkourPlayer.offlinePlayer.getName() + " : " + parkourPlayer.getParkourScore();
            component = component.appendNewline().append(
                    Component.text(scoreString).color(TextColor.color(175, 174, 252))
            );
        }
        component = component.appendNewline().append(Component.text("---------跑酷得分排行榜---------").color(TextColor.color(55, 218, 250)));
        player.sendMessage(component);
    }
}
