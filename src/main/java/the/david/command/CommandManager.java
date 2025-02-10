package the.david.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import the.david.command.playerManage.ListParkourScore;
import the.david.command.playerManage.ParkourPlayerInfo;
import the.david.command.playerManage.ResetParkourPlayer;
import the.david.command.setup.*;
import the.david.command.teleport.Back;
import the.david.command.teleport.TeleportParkour;
import the.david.impl.Pair;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor{
	public CommandManager(){
		subCommands.put("setup init {parkourID} (toCenter)", new SetupChooseLocation());
		subCommands.put("setup pressure {parkourID} {SubParkourID}", new SetupChoosePressure());
		subCommands.put("setup parkour {parkourID} {SubParkourID} (toCenter)", new SetupParkourLocation());
		subCommands.put("setup message {parkourID} {SubParkourID} {message}", new SetupParkourMessage());
		subCommands.put("setup finish {parkourID}", new SetupFinishPressure());
		subCommands.put("setup difficulty {parkourID} {difficulty}", new SetupDifficulty());
		subCommands.put("setup score {parkourID} {score}", new SetupScore());
		subCommands.put("setup practicable {parkourID} {practicable}", new SetupCanUsePractice());
		subCommands.put("teleport parkour {parkourID} (SubParkourID)", new TeleportParkour());
		subCommands.put("list", new ListParkourScore());
		subCommands.put("info", new ParkourPlayerInfo());
		Back back = new Back();
		subCommands.put("back", back);
		subCommands.put("teleport back", back);
		subCommands.put("reset (player)", new ResetParkourPlayer());
	}

	public static final Map<String, SubCommand> subCommands = new HashMap<>();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args){
		if(!(sender instanceof Player)){
			return false;
		}
		Player player = (Player) sender;


		Pair<SubCommand, Map<String, String>> matchedData = matchCommand(args, player.isOp());
		if(matchedData != null){
			matchedData.getKey().execute(player, matchedData.getValue());
			return true;
		}else{
			// 提示玩家該指令不存在或其他默認行為
			player.sendMessage(
					Component.text("該指令不存在").color(NamedTextColor.RED));
		}

		return false;
	}

	private Pair<SubCommand, Map<String, String>> matchCommand(String[] args, Boolean isOp){
		for(Map.Entry<String, SubCommand> entry : subCommands.entrySet()){
			if(entry.getValue().opOnly() && !isOp){
				continue;
			}
			String[] commandParts = entry.getKey().split(" ");
			int needLength = commandParts.length;
			int mexLength = commandParts.length;
			for(String commandPart : commandParts){
				if(commandPart.startsWith("(") && commandPart.endsWith(")")){
					needLength--;
				}
				if(commandPart.contains("{message}")){
					mexLength = Integer.MAX_VALUE;
				}
			}
			if(args.length < needLength || args.length > mexLength){
				continue;
			}

			Map<String, String> parsedArgs = new HashMap<>();
			boolean matches = true;
			for(int i = 0; i < commandParts.length; i++){
				if(commandParts[i].startsWith("{") && commandParts[i].endsWith("}")){
					String paramName = commandParts[i].substring(1, commandParts[i].length() - 1);
					if(commandParts[i].equals("{message}")){
						StringBuilder message = new StringBuilder();
						for(int j = i; j < args.length; j++){
							message.append(args[j]);
							message.append(" ");
						}
						message.deleteCharAt(message.length() - 1);
						parsedArgs.put(paramName, message.toString());
					}else{
						parsedArgs.put(paramName, args[i]);
					}

				}else if(commandParts[i].startsWith("(") && commandParts[i].endsWith(")")){
					String paramName = commandParts[i].substring(1, commandParts[i].length() - 1);
					if(args.length > i){
						parsedArgs.put(paramName, args[i]);
					}
				}else if(!commandParts[i].equals(args[i])){
					matches = false;
					break;
				}
			}

			if(matches){
				return new Pair<>(entry.getValue(), parsedArgs);
			}
		}
		return null;
	}
}
