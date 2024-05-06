package the.david.handler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
                        int handSlot = player.getInventory().getHeldItemSlot();
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        new BukkitRunnable(){
                            @Override
                            public void run() {
//                                player.getInventory().setItemInMainHand(leavePracticeItem);
                                player.getInventory().setItem(handSlot, leavePracticeItem);
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
                parkourPlayer.leavePractice();
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
                e.setCancelled(false);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                ParkourLocation parkourLocation = ParkourLocationManager.parkourPressures.get(steppedLocation);
                parkourPlayer.playingParkourID = parkourLocation.getParkourID();
                parkourPlayer.selectedSubParkourID = parkourLocation.getSteppedPressureSubParkourID(steppedLocation);
                String messsageString = parkourLocation.getParkourMessage(parkourPlayer.selectedSubParkourID);
                TextColor parkourIdColor = ParkourLocationManager.getParkourIdColor(parkourPlayer.playingParkourID);
                if(messsageString != null && !messsageString.isEmpty()){
                    Component fullMessage = Component.text().append(Component.text(parkourPlayer.playingParkourID + ". ").color(parkourIdColor)).build();
                    Component message = Component.text().append(MiniMessage.miniMessage().deserialize(messsageString)).build();
                    player.sendMessage(fullMessage.append(message));
                }
                Component component = Component.text().append(Component.text("已選擇跑酷 ").color(NamedTextColor.BLUE)).build();
                component = component.append(Component.text(parkourPlayer.playingParkourID + "-" + parkourPlayer.selectedSubParkourID).color(parkourIdColor));
                player.sendActionBar(component);
            }else if(ParkourLocationManager.finishLocations.containsKey(steppedLocation)){
                e.setCancelled(false);
                ParkourLocation parkourLocation = ParkourLocationManager.finishLocations.get(steppedLocation);
                parkourPlayer.addFinishedParkourID(parkourLocation.getParkourID());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
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