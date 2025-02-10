package the.david.command.playerManage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import the.david.command.SubCommand;
import the.david.impl.ParkourPlayer;
import the.david.manager.PlayerManager;

import java.util.List;
import java.util.Map;

public class ListParkourScore implements SubCommand{
	@Override
	public Boolean opOnly(){
		return false;
	}

	@Override
	public void execute(Player player, Map<String, String> parsedArgs){
		Component component = Component.text().append(Component.text("---------跑酷得分排行榜---------").color(TextColor.color(55, 218, 250))).build();
		ParkourPlayer parkourPlayer = PlayerManager.getParkourPlayer(player);
		List<ParkourPlayer> parkourPlayers = PlayerManager.getParkourPlayerScores();
		int rankNow = 1, playerCount = 0, yourRank = 0;
		for(int i = 0; i < parkourPlayers.size(); i++){
			ParkourPlayer rankParkourPlayer = parkourPlayers.get(i);
			int score = rankParkourPlayer.getParkourScore();
			if(i > 0 &&score != parkourPlayers.get(i-1).getParkourScore()){
				rankNow++;
			}
			if(score != 0){
				playerCount++;
				String scoreString = " " + rankNow + ". " + rankParkourPlayer.offlinePlayer.getName() + " : " + score;
				component = component.appendNewline().append(
						Component.text(scoreString).color(TextColor.color(175, 174, 252))
				);
				if(parkourPlayer == rankParkourPlayer){
					yourRank = rankNow;
				}
			}
		}
//		for(ParkourPlayer parkourPlayer : parkourPlayers){
//			int score = parkourPlayer.getParkourScore();
//			if(score != 0){
//				String scoreString = "  " + parkourPlayer.offlinePlayer.getName() + " : " + score;
//				component = component.appendNewline().append(
//						Component.text(scoreString).color(TextColor.color(175, 174, 252))
//				);
//			}
//		}
		component = component.appendNewline().append(Component.text("---------跑酷得分排行榜---------").color(TextColor.color(55, 218, 250)));
		player.sendMessage(component);
		player.sendMessage(Component.text("跑酷參加人數: " + playerCount).color(TextColor.color(96, 160, 255)));
		player.sendMessage(Component.text("您的名次: " + yourRank).color(TextColor.color(107, 125, 255)));
	}
}
