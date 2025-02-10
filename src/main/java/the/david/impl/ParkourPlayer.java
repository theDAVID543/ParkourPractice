package the.david.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import the.david.handler.DataHandler;
import the.david.handler.EventHandler;
import the.david.manager.ParkourLocationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static the.david.Main.plugin;

public class ParkourPlayer{
	public Player player;
	public OfflinePlayer offlinePlayer;
	Location practiceCheckpoint;
	Boolean inPractice = false;
	BossBar bossBar;
	int score = 0;
	public Integer playingParkourID;
	public Integer selectedSubParkourID;
	Set<Integer> finishedParkourIDs = new HashSet<>();
	String configPath;

	public ParkourPlayer(OfflinePlayer offlinePlayer){
		this.offlinePlayer = offlinePlayer;
		if(offlinePlayer.getPlayer() != null){
			player = offlinePlayer.getPlayer();
		}
		configPath = "ParkourPlayers." + offlinePlayer.getUniqueId();
		loadData();
	}

	void loadData(){
		finishedParkourIDs = new HashSet<>(DataHandler.getIntegerList(configPath + ".FinishedParkour"));
		playingParkourID = DataHandler.getInt(configPath + ".PlayingParkourID");
		selectedSubParkourID = DataHandler.getInt(configPath + ".SelectedSubParkourID");
		Boolean inPractice = DataHandler.getBoolean(configPath + ".InPractice");
		if(Boolean.TRUE.equals(inPractice)){
			this.inPractice = true;
			practiceCheckpoint = DataHandler.getLocation(configPath + ".PracticeCheckpoint");
			showBossBar();
		}
		score = DataHandler.getInt(configPath + ".Score");
	}

	public void setPlayingParkourID(int id){
		this.playingParkourID = id;
		DataHandler.setInt(configPath + ".PlayingParkourID", id);
	}

	public void setSelectedSubParkourID(int id){
		this.selectedSubParkourID = id;
		DataHandler.setInt(configPath + ".SelectedSubParkourID", id);
	}

	public void setInPractice(Boolean inPractice){
		this.inPractice = inPractice;
		DataHandler.setBoolean(configPath + ".InPractice", inPractice);
	}

	public void setPracticeCheckpoint(Location practiceCheckpoint){
		this.practiceCheckpoint = practiceCheckpoint;
		DataHandler.setLocation(configPath + ".PracticeCheckpoint", practiceCheckpoint);
	}

	public void addScore(int score){
		this.score += score;
		DataHandler.setInt(configPath + ".Score", this.score);
	}

	public void enterPractice(Location checkpointLocation){
		setPracticeCheckpoint(checkpointLocation);
		showBossBar();
		setInPractice(true);
	}
	public void showBossBar(){
		if(bossBar == null){
			bossBar = Bukkit.createBossBar("練習模式", BarColor.WHITE, BarStyle.SOLID);
		}
		if(player != null){
			bossBar.addPlayer(player);
		}
	}

	public Location getPracticeCheckpoint(){
		return practiceCheckpoint;
	}

	public int getParkourScore(){
		return score;
	}

	public void removeBossBar(){
		if(bossBar != null){
			bossBar.removeAll();
		}
	}

	public void leavePractice(){
		if(inPractice){
			boolean teleportSucceed = player.teleport(practiceCheckpoint);
			if(!teleportSucceed){
				player.sendMessage(
						Component.text("離開練習模式錯誤，請重新進入伺服器").color(NamedTextColor.RED)
				);
			}else{
				setInPractice(false);
				setPracticeCheckpoint(null);
				removeBossBar();
				player.sendMessage(Component.text("已傳送回紀錄點並離開練習模式").color(NamedTextColor.GREEN));
				ItemStack enterPracticeItem = new ItemStack(Material.ENDER_EYE);
				ItemMeta enterPracticeItemMeta = enterPracticeItem.getItemMeta();
				enterPracticeItemMeta.displayName(Component.text("進入練習模式").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
				enterPracticeItem.setItemMeta(enterPracticeItemMeta);
				int handSlot = player.getInventory().getHeldItemSlot();
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				new BukkitRunnable(){
					@Override
					public void run(){
						player.getInventory().setItem(handSlot, enterPracticeItem);
					}
				}.runTaskLater(plugin, 10L);
			}
		}else{
			player.sendMessage(Component.text("不在練習模式中").color(NamedTextColor.RED));
		}
	}

	public boolean isInPractice(){
		return inPractice;
	}

	public void teleportToCheckpoint(boolean isFall){
		EventHandler.bypassPlayers.add(player);
		player.clearActivePotionEffects();
		ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(playingParkourID);
		if(parkourLocation == null){
			if(!isFall){
				player.sendMessage(
						Component.text("選擇的跑酷ID無效").color(NamedTextColor.RED)
				);
			}
			return;
		}
		Location location = parkourLocation.getSubParkourLocation(selectedSubParkourID);
		if(location == null){
			if(!isFall){
				player.sendMessage(
						Component.text("選擇的子跑酷ID無效").color(NamedTextColor.RED)
				);
			}else{
				player.teleport(parkourLocation.getChooseMethodLocation());
			}
			return;
		}
		player.teleport(location);
		player.sendActionBar(
				Component.text("已傳送至跑酷 ").color(NamedTextColor.GREEN)
						.append(Component.text(playingParkourID + "-" + selectedSubParkourID).color(ParkourLocationManager.getParkourIdColor(playingParkourID)))
		);
		EventHandler.bypassPlayers.remove(player);
	}

	public Set<Integer> getFinishedParkourIDs(){
		return finishedParkourIDs;
	}

	public void addFinishedParkourID(int id){
		ParkourLocation parkourLocation = ParkourLocationManager.getParkourLocation(id);
		boolean didntJump = !finishedParkourIDs.contains(id);
		if(didntJump){
			addScore(parkourLocation.score);
			finishedParkourIDs.add(id);
			List<Integer> list = new ArrayList<>(finishedParkourIDs);
			DataHandler.setIntegerList(configPath + ".FinishedParkour", list);
		}
		ParkourLocation nextParkourLocation = ParkourLocationManager.getParkourLocation(id + 1);
		Component component = Component.text().append(Component.text("恭喜完成跑酷 " + id).color(TextColor.color(36, 237, 126))).build();
		if(nextParkourLocation != null && nextParkourLocation.getChooseMethodLocation() != null && id != 0){
			component = component.append(Component.text(" 已傳送至跑酷 " + (id + 1)).color(TextColor.color(28, 225, 232)));
			player.teleport(nextParkourLocation.getChooseMethodLocation());
		}
		if(didntJump){
			component = component.appendNewline().append(
					Component.text("已添加 " + parkourLocation.score + " 點跑酷點數").color(TextColor.color(45, 237, 228))
			);
		}
		player.sendMessage(component);
	}

	public void resetAll(){
		DataHandler.setObject(configPath, null);

		finishedParkourIDs = new HashSet<>();
		playingParkourID = null;
		selectedSubParkourID = null;
		inPractice = false;
		practiceCheckpoint = null;
		score = 0;
	}
}
