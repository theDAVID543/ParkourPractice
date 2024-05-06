package the.david.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import the.david.handler.DataHandler;
import the.david.manager.ParkourLocationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static the.david.Main.plugin;

public class ParkourPlayer{
    public Player player;
    Location practiceCheckpoint;
    boolean inPractice = false;
    BossBar bossBar;
    public int playingParkourID;
    public int selectedSubParkourID;
    Set<Integer> finishedParkourIDs = new HashSet<>();
    String configPath;
    public ParkourPlayer(OfflinePlayer offlinePlayer){
        if(offlinePlayer.isOnline()){
            player = offlinePlayer.getPlayer();
        }
        configPath = "ParkourPlayers." + offlinePlayer.getUniqueId();
        loadData();
    }
    void loadData(){
        finishedParkourIDs = new HashSet<>(DataHandler.getIntegerList(configPath + ".FinishedParkour"));
    }
    public void enterPractice(Location checkpointLocation){
        inPractice = true;
        this.practiceCheckpoint = checkpointLocation;
        bossBar = Bukkit.createBossBar("練習模式", BarColor.WHITE, BarStyle.SOLID);
        bossBar.addPlayer(player);
    }
    public Location getPracticeCheckpoint(){
        return practiceCheckpoint;
    }
    public int getParkourScore(){
        return finishedParkourIDs.size();
    }
    public void leavePractice(){
        if(inPractice){
            if(!player.teleport(practiceCheckpoint)){
                player.sendMessage(Component.text("離開練習模式錯誤，請重新進入伺服器").color(NamedTextColor.RED));
            }
            inPractice = false;
            practiceCheckpoint = null;
            bossBar.removeAll();
            player.sendMessage(Component.text("已傳送回紀錄點並離開練習模式").color(NamedTextColor.GREEN));
            ItemStack enterPracticeItem = new ItemStack(Material.ENDER_EYE);
            ItemMeta enterPracticeItemMeta = enterPracticeItem.getItemMeta();
            enterPracticeItemMeta.displayName(Component.text("進入練習模式").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
            enterPracticeItem.setItemMeta(enterPracticeItemMeta);
            int handSlot = player.getInventory().getHeldItemSlot();
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.getInventory().setItem(handSlot, enterPracticeItem);
                }
            }.runTaskLaterAsynchronously(plugin, 1L);
        }else {
            player.sendMessage(Component.text("不在練習模式中").color(NamedTextColor.RED));
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
    public Set<Integer> getFinishedParkourIDs(){
        return finishedParkourIDs;
    }
    public void addFinishedParkourID(int id){
        finishedParkourIDs.add(id);
        List<Integer> list = new ArrayList<>(finishedParkourIDs);
        DataHandler.setIntegerList(configPath + ".FinishedParkour", list);
        ParkourLocation nextParkourLocation = ParkourLocationManager.getParkourLocation(id + 1);
        Component component = Component.text().append(Component.text("恭喜完成跑酷 " + id).color(TextColor.color(36, 237, 126))).build();
        if(nextParkourLocation != null && nextParkourLocation.getChooseMethodLocation() != null){
            component = component.append(Component.text(" 已傳送至跑酷 " + (id + 1)).color(TextColor.color(28, 225, 232)));
            player.teleport(nextParkourLocation.getChooseMethodLocation());
        }
        player.sendMessage(component);
    }
}
