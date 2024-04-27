package the.david.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import the.david.Main;
import the.david.command.setup.SetupChooseLocation;
import the.david.command.setup.SetupChoosePressure;
import the.david.command.setup.SetupParkourLocation;
import the.david.command.teleport.TeleportParkour;
import the.david.impl.Pair;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements CommandExecutor {
    public CommandManager() {
        subCommands.put("setup init {parkourID} (toCenter)", new SetupChooseLocation());
        subCommands.put("setup pressure {parkourID} {SubParkourID}", new SetupChoosePressure());
        subCommands.put("setup parkour {parkourID} {SubParkourID} (resetYaw)", new SetupParkourLocation());
        subCommands.put("teleport {parkourID} (SubParkourID)", new TeleportParkour());
    }
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;


        Pair<SubCommand, Map<String, String>> matchedData = matchCommand(args);
        if (matchedData != null) {
            matchedData.getKey().execute(player, matchedData.getValue());
        } else {
            // 提示玩家該指令不存在或其他默認行為
            player.sendMessage(
                    Component.text("該指令不存在").color(NamedTextColor.RED));
        }

        return false;
    }
    private Pair<SubCommand, Map<String, String>> matchCommand(String[] args) {
        for (Map.Entry<String, SubCommand> entry : subCommands.entrySet()) {
            String[] commandParts = entry.getKey().split(" ");
            int needLength = commandParts.length;
            for (String commandPart : commandParts) {
                if (commandPart.startsWith("(") && commandPart.endsWith(")")){
                    needLength--;
                }
            }
            if (!(args.length >= needLength && args.length <= commandParts.length)){
                continue;
            }

            Map<String, String> parsedArgs = new HashMap<>();
            boolean matches = true;
            for (int i = 0; i < commandParts.length; i++) {
                if (commandParts[i].startsWith("{") && commandParts[i].endsWith("}")) {
                    String paramName = commandParts[i].substring(1, commandParts[i].length() - 1);
                    parsedArgs.put(paramName, args[i]);
                }else if(commandParts[i].startsWith("(") && commandParts[i].endsWith(")")){
                    String paramName = commandParts[i].substring(1, commandParts[i].length() - 1);
                    if(args.length > i){
                        parsedArgs.put(paramName, args[i]);
                    }
                }else if (!commandParts[i].equals(args[i])) {
                    matches = false;
                    break;
                }
            }

            if (matches) {
                return new Pair<>(entry.getValue(), parsedArgs);
            }
        }
        return null;
    }
}
