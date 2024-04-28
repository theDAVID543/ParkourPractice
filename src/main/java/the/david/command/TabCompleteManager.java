package the.david.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import the.david.handler.DebugOutputHandler;
import the.david.impl.Pair;
import the.david.impl.ParkourDifficulty;

import java.util.*;

public class TabCompleteManager implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args){
        if(!(sender instanceof Player)){
            return null;
        }
        Player player = (Player) sender;
        return matchCommand(args);
    }
    private List<String> matchCommand(String[] args) {
        List<String> matchList = new ArrayList<>();
        for (Map.Entry<String, SubCommand> entry : CommandManager.subCommands.entrySet()) {
            String[] commandParts = entry.getKey().split(" ");
            int needLength = commandParts.length;
            if (args.length > needLength){
                continue;
            }

            boolean matches = true;
            for (int i = 0; i < args.length - 1; i++) {
                boolean isHitArg = commandParts[i].startsWith("{") && commandParts[i].endsWith("}") || commandParts[i].startsWith("(") && commandParts[i].endsWith(")");
                if(isHitArg && commandParts[i+1].equals("{difficulty}")){
                    List<ParkourDifficulty> parkourDifficultyList = Arrays.asList(ParkourDifficulty.values());
                    parkourDifficultyList.forEach(parkourDifficulty -> {
                        matchList.add(parkourDifficulty.toString());
                    });
                }
                if(!Objects.equals(args[i], commandParts[i]) && !isHitArg){
                    matches = false;
                    break;
                }
            }

            if (matches) {
                matchList.add(commandParts[args.length - 1]);
            }
        }
        return matchList;
    }
}
