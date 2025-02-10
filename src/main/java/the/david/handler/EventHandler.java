package the.david.handler;

import me.frep.vulcan.api.VulcanAPI;
import me.frep.vulcan.api.data.IPlayerData;
import me.frep.vulcan.api.event.VulcanFlagEvent;
import me.frep.vulcan.api.event.VulcanPostFlagEvent;
import me.frep.vulcan.api.event.VulcanPunishEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import the.david.impl.ParkourDifficulty;
import the.david.impl.ParkourLocation;
import the.david.impl.ParkourPlayer;
import the.david.manager.ParkourLocationManager;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static the.david.Main.plugin;
import static the.david.manager.PlayerManager.getParkourPlayer;

public class EventHandler implements Listener{
	LuckPerms luckPerms;

	public EventHandler(){

	}

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
			case ENDER_EYE -> {
				if(!itemDisplayName.contains("進入練習模式")){
					return;
				}
				e.setCancelled(true);
				if(!((Entity) player).isOnGround()){
					player.sendMessage(Component.text("需站在地上").color(NamedTextColor.RED));
					break;
				}
				if(parkourPlayer.isInPractice()){
					player.sendMessage(Component.text("已在練習模式中").color(NamedTextColor.RED));
					break;
				}
				if(parkourPlayer.playingParkourID == null){
					player.sendMessage(Component.text("不在跑酷中").color(NamedTextColor.RED));
					break;
				}
				ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(parkourPlayer.playingParkourID);
				if(parkourLocation == null){
					player.sendMessage(Component.text("不在跑酷中").color(NamedTextColor.RED));
					break;
				}
				if(!parkourLocation.getCanUsePractice()){
					player.sendMessage(Component.text("此跑酷無法使用練習模式").color(NamedTextColor.RED));
					break;
				}
				player.sendMessage(Component.text("已進入練習模式並設置紀錄點").color(NamedTextColor.GREEN));
				parkourPlayer.enterPractice(player.getLocation());
				ItemStack leavePracticeItem = new ItemStack(Material.MAGMA_CREAM);
				ItemMeta leavePracticeItemMeta = leavePracticeItem.getItemMeta();
				leavePracticeItemMeta.displayName(Component.text("離開練習模式").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED));
				leavePracticeItem.setItemMeta(leavePracticeItemMeta);
				int handSlot = player.getInventory().getHeldItemSlot();
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				new BukkitRunnable(){
					@Override
					public void run(){
						player.getInventory().setItem(handSlot, leavePracticeItem);
					}
				}.runTaskLaterAsynchronously(plugin, 10L);
			}
			case ENDER_PEARL -> {
				if(!itemDisplayName.contains("傳送回記錄點")){
					return;
				}
				e.setCancelled(true);
				if(!parkourPlayer.isInPractice()){
					parkourPlayer.teleportToCheckpoint(false);
				}else{
					bypassPlayers.add(player);
					player.teleport(parkourPlayer.getPracticeCheckpoint());
					bypassPlayers.remove(player);
					player.sendActionBar(Component.text("已傳送回練習紀錄點").color(NamedTextColor.GREEN));
				}
			}
			case MAGMA_CREAM -> {
				if(!itemDisplayName.contains("離開練習模式")){
					return;
				}
				e.setCancelled(true);
				parkourPlayer.leavePractice();
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
				e.setCancelled(false);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
				ParkourLocation parkourLocation = ParkourLocationManager.parkourPressures.get(steppedLocation);
				parkourPlayer.setPlayingParkourID(parkourLocation.getParkourID());
				parkourPlayer.setSelectedSubParkourID(parkourLocation.getSteppedPressureSubParkourID(steppedLocation));
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
				if(parkourLocation.getFinishLocation() != null){
					Component message = Component.text().append(Component.text("完成此跑酷可獲得 " + parkourLocation.getScore() + " 分").color(parkourIdColor)).build();
					player.sendMessage(message);
				}
			}else if(ParkourLocationManager.finishLocations.containsKey(steppedLocation)){
				e.setCancelled(false);
				ParkourLocation parkourLocation = ParkourLocationManager.finishLocations.get(steppedLocation);
				parkourPlayer.addFinishedParkourID(parkourLocation.getParkourID());
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				if(parkourLocation.getDifficulty() == ParkourDifficulty.BLACK || parkourLocation.getDifficulty() == ParkourDifficulty.WHITE){
					Component globalMessage = Component.text()
							.append(Component.text("恭喜玩家 ").color(NamedTextColor.RED))
							.append(player.name().color(NamedTextColor.GOLD))
							.append(Component.text(" 完成跑酷 : ").color(NamedTextColor.RED))
							.append(Component.text(parkourLocation.getParkourID()).color(ParkourLocationManager.getParkourIdColor(parkourLocation.getParkourID())))
							.build();
					Bukkit.getServer().sendMessage(globalMessage);
					Bukkit.getServer().playSound(net.kyori.adventure.sound.Sound.sound().type(Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON).build());
				}
			}
		}
	}

	@org.bukkit.event.EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		ParkourPlayer parkourPlayer = getParkourPlayer(player);
		if(parkourPlayer.isInPractice()){
			ItemStack leavePracticeItem = new ItemStack(Material.MAGMA_CREAM);
			ItemMeta leavePracticeItemMeta = leavePracticeItem.getItemMeta();
			leavePracticeItemMeta.displayName(Component.text("離開練習模式").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED));
			leavePracticeItem.setItemMeta(leavePracticeItemMeta);
			new BukkitRunnable(){
				@Override
				public void run(){
					player.getInventory().setItem(8, leavePracticeItem);
					parkourPlayer.showBossBar();
				}
			}.runTaskLater(plugin, 20L);
			player.updateInventory();
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

	@org.bukkit.event.EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		Location from = e.getFrom();
		Location to = e.getTo();
		Player player = e.getPlayer();
		if(player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)){
			return;
		}
		if(!to.getWorld().getName().equals("world")){
			return;
		}
		if(from.getBlockY() == to.getBlockY()){
			return;
		}
		if(to.getBlockY() > 122){
			return;
		}
		Location location = to.clone();
		for(int i = to.getBlockY(); i > 85 && i > to.getBlockY() - 30; i--){
			location.setY(i);
			if(!(location.getBlock().getType().equals(Material.AIR) || location.getBlock().getType().equals(Material.RED_CONCRETE))){
				return;
			}
		}
		ParkourPlayer parkourPlayer = getParkourPlayer(player);
		bypassPlayers.add(player);
		if(!parkourPlayer.isInPractice()){
			parkourPlayer.teleportToCheckpoint(true);
		}else{
			player.teleport(parkourPlayer.getPracticeCheckpoint());
			player.sendActionBar(Component.text("已傳送回練習紀錄點").color(NamedTextColor.GREEN));
		}
		bypassPlayers.remove(player);
	}
	public static final Set<Player> bypassPlayers = new HashSet<>();
	@org.bukkit.event.EventHandler
	public void vulcanPostFlagEvent(VulcanPostFlagEvent e){
		if(bypassPlayers.contains(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	@org.bukkit.event.EventHandler
	public void vulcanFlagEvent(VulcanFlagEvent e){
		if(bypassPlayers.contains(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	@org.bukkit.event.EventHandler
	public void onPlayerPunish(VulcanPunishEvent e){
		if(bypassPlayers.contains(e.getPlayer())){
			e.setCancelled(true);
		}
	}
}