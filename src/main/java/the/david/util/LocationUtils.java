package the.david.util;

import org.bukkit.Location;

public class LocationUtils{
	public static Location toParkourCenter(Location location, boolean resetYaw){
		location = location.toCenterLocation();
		location.setY(location.getBlockY());
		if(resetYaw){
			location.setYaw(0);
		}
		location.setPitch(0);
		return location;
	}

	public static String getLocationString(Location location){
		return "(" + location.getX() +
				", " + location.getY() +
				", " + location.getZ() +
				") Yaw: " + location.getYaw() +
				" Pitch: " + location.getPitch();
	}
}
