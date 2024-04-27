package the.david.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import the.david.impl.ParkourLocation;
import the.david.impl.ParkourPlayer;
import the.david.manager.ParkourLocationManager;

import java.time.Duration;

import static the.david.Main.plugin;
import static the.david.manager.PlayerManager.getParkourPlayer;

public class EventHandler implements Listener {
    @org.bukkit.event.EventHandler
    public void onRightClick(PlayerInteractEvent e){
        if(!e.getAction().isRightClick() || !e.hasItem() || e.getHand() != EquipmentSlot.HAND){
            return;
        }
        if(e.getItem() == null){
            return;
        }
        Player player = e.getPlayer();
        ParkourPlayer parkourPlayer = getParkourPlayer(player);
        ItemStack item = e.getItem();
        String itemDisplayName = PlainTextComponentSerializer.plainText().serialize(item.displayName());
        switch(item.getType()){
            case ENDER_EYE:
                if(!itemDisplayName.contains("進入練習模式")){
                    return;
                }
                e.setCancelled(true);
                if(((Entity)player).isOnGround()) {
                    if(parkourPlayer.isInPractice()){
                        player.sendMessage(Component.text("已在練習模式中").color(NamedTextColor.RED));
                    }else {
                        player.sendMessage(Component.text("已進入練習模式並設置紀錄點").color(NamedTextColor.GREEN));
                        parkourPlayer.enterPractice(player.getLocation());
                        ItemStack leavePracticeItem = new ItemStack(Material.BARRIER);
                        ItemMeta leavePracticeItemMeta = leavePracticeItem.getItemMeta();
                        leavePracticeItemMeta.displayName(Component.text("離開練習模式").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED));
                        leavePracticeItem.setItemMeta(leavePracticeItemMeta);
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                player.getInventory().setItemInMainHand(leavePracticeItem);
                            }
                        }.runTaskLaterAsynchronously(plugin, 1L);
                    }
                }else{
                    player.sendMessage(Component.text("需站在地上").color(NamedTextColor.RED));
                }
                break;
            case ENDER_PEARL:
                if(!itemDisplayName.contains("傳送回記錄點")){
                    return;
                }
                e.setCancelled(true);
                if(!parkourPlayer.isInPractice()){
//                    player.sendMessage(Component.text("不在練習模式中").color(NamedTextColor.RED));
                    parkourPlayer.teleportToCheckpoint();
                }else{
                    player.teleport(parkourPlayer.getPracticeCheckpoint());
                    player.sendActionBar(Component.text("已傳送回練習紀錄點").color(NamedTextColor.GREEN));
                }
                break;
            case BARRIER:
                if(!itemDisplayName.contains("離開練習模式")){
                    return;
                }
                e.setCancelled(true);
                if(!parkourPlayer.isInPractice()){
                    player.sendMessage(Component.text("不在練習模式中").color(NamedTextColor.RED));
                }else{
                    parkourPlayer.leavePractice();
//                    player.sendMessage("已傳送回紀錄點並離開練習模式");
                    player.sendMessage(Component.text("已傳送回紀錄點並離開練習模式").color(NamedTextColor.GREEN));
                    ItemStack enterPracticeItem = new ItemStack(Material.ENDER_EYE);
                    ItemMeta enterPracticeItemMeta = enterPracticeItem.getItemMeta();
                    enterPracticeItemMeta.displayName(Component.text("進入練習模式").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
                    enterPracticeItem.setItemMeta(enterPracticeItemMeta);
                    new BukkitRunnable(){

                        @Override
                        public void run() {
                            player.getInventory().setItemInMainHand(enterPracticeItem);
                        }
                    }.runTaskLaterAsynchronously(plugin, 1L);
                }
        }
    }
    @org.bukkit.event.EventHandler
    public void onStepPressure(PlayerInteractEvent e){
        if(e.getAction() != Action.PHYSICAL){
            return;
        }
        if(!e.hasBlock()){
            return;
        }
        if(e.getClickedBlock().getType() != Material.STONE_PRESSURE_PLATE){
            return;
        }
        Player player = e.getPlayer();
        ParkourPlayer parkourPlayer = getParkourPlayer(player);
        if(parkourPlayer.isInPractice()){
            e.setCancelled(true);
            Title title = Title.title(Component.text().build(), Component.text("練習模式無法踩壓力版").color(NamedTextColor.RED), Title.Times.times(Duration.ofSeconds(0), Duration.ofMillis(100), Duration.ofSeconds(0)));
            player.showTitle(title);
        }else{
            Location steppedLocation = e.getClickedBlock().getLocation().toBlockLocation();
            if(ParkourLocationManager.parkourPressures.containsKey(steppedLocation)){
                ParkourLocation parkourLocation = ParkourLocationManager.parkourPressures.get(steppedLocation);
                parkourPlayer.playingParkourID = parkourLocation.parkourID;
                parkourPlayer.selectedSubParkourID = parkourLocation.getSteppedPressureSubParkourID(steppedLocation);
                player.sendActionBar(
                        Component.text("已選擇跑酷 " + parkourPlayer.playingParkourID + "-" + parkourPlayer.selectedSubParkourID).color(NamedTextColor.BLUE)
                );
            }
        }
    }
    @org.bukkit.event.EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player player = e.getPlayer();
        ParkourPlayer parkourPlayer = getParkourPlayer(player);
        if(parkourPlayer.isInPractice()){
            parkourPlayer.leavePractice();
        }
    }
    @org.bukkit.event.EventHandler
    public void onClickOnDragonEgg(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        if(!e.hasBlock()){
            return;
        }
        if(e.getClickedBlock().getType() == Material.DRAGON_EGG){
            e.setCancelled(true);
        }
    }
}
