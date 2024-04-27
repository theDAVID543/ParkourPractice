package the.david.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import the.david.manager.ParkourLocationManager;

public class ParkourPlayer{
    Player player;
    Location practiceCheckpoint;
    boolean inPractice = false;
    BossBar bossBar;
    public int playingParkourID;
    public int selectedSubParkourID;
    public ParkourPlayer(Player player){
        this.player = player;
    }
    public void enterPractice(Location checkpointLocation){
        inPractice = true;
        this.practiceCheckpoint = checkpointLocation;
        bossBar = Bukkit.createBossBar("練習模式", BarColor.WHITE, BarStyle.SOLID);
        bossBar.addPlayer(player);
        performCommandAsOp("tag @s add practice");
    }
    public Location getPracticeCheckpoint(){
        return practiceCheckpoint;
    }
    public void leavePractice(){
        if(inPractice){
            player.teleport(practiceCheckpoint);
            inPractice = false;
            practiceCheckpoint = null;
            bossBar.removeAll();
            performCommandAsOp("tag @s remove practice");
        }
    }
    public boolean isInPractice(){
        return inPractice;
    }
    public void teleportToCheckpoint(){
        player.clearActivePotionEffects();
        ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(playingParkourID);
        if(parkourLocation == null){
            player.sendMessage(
                    Component.text("選擇的跑酷ID無效").color(NamedTextColor.RED)
            );
            return;
        }
        Location location = parkourLocation.getSubParkourLocation(selectedSubParkourID);
        if(location == null){
            player.sendMessage(
                    Component.text("選擇的子跑酷ID無效").color(NamedTextColor.RED)
            );
            return;
        }
        player.teleport(location);
        player.sendActionBar(Component.text("已傳送至跑酷 " + playingParkourID + "-" + selectedSubParkourID).color(NamedTextColor.GREEN));
    }
    void performCommandAsOp(String command){
        boolean isOp = player.isOp();
        boolean sendCommandFeedback = Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK));
        boolean logAdminCommands = Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.LOG_ADMIN_COMMANDS));
        try {
            player.setOp(true);
            player.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
            player.getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
            Bukkit.dispatchCommand(player, command);
            player.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, sendCommandFeedback);
            player.getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, logAdminCommands);
            player.setOp(isOp);
        } catch (Exception exc) {
            player.setOp(isOp);
            player.sendMessage("Error in op command!");
        }
    }
}
