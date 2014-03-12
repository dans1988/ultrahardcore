package uk.co.eluinhost.ultrahardcore.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import uk.co.eluinhost.ultrahardcore.UltraHardcore;

@SuppressWarnings("UtilityClass")
public final class ServerUtil {

    /**
     * Misc server related methods
     */
    private ServerUtil() {}

    /**
     * Send the message to all players with the permission
     * @param message the message to send
     * @param perm the permission to check
     */
    public static void broadcastForPermission(String message, String perm) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(perm)) {
                p.sendMessage(message);
            }
        }
    }

    /**
     * Sends a player to another bungee server
     * @param recipient the player
     * @param serverName the server name to send them to
     */
    public static void sendPlayerToServer(PluginMessageRecipient recipient, String serverName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(outputStream);

        try {
            out.writeUTF("Connect");
            out.writeUTF(serverName);
        } catch (IOException ignored) {
            Bukkit.getLogger().log(Level.SEVERE,"Error writing to plugin output stream");
        }

        recipient.sendPluginMessage(UltraHardcore.getInstance(), "BungeeCord", outputStream.toByteArray());
    }

    /**
     * @return a list of all online players by name
     */
    public static List<String> getOnlinePlayers() {
        List<String> p = new ArrayList<String>();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            p.add(pl.getName());
        }
        return p;
    }

    /**
     * @return a list of all the world names
     */
    public static String[] getWorldNames() {
        List<String> p = new ArrayList<String>();
        for (World w : Bukkit.getWorlds()) {
            p.add(w.getName());
        }
        return p.toArray(new String[p.size()]);
    }

    /**
     * @return a list of worldnames with the spawn coordinates appended
     */
    public static List<String> getWorldNamesWithSpawn() {
        List<String> p = new ArrayList<String>();
        for (World w : Bukkit.getWorlds()) {
            Location location = w.getSpawnLocation();
            p.add(w.getName() + ":" + location.getBlockX() + "," + location.getBlockZ());
        }
        return p;
    }

    /**
     * Gets the closest blockface to the entities facing direction
     * @param entity the entity
     * @return block face
     */
    public static BlockFace getCardinalDirection(Entity entity) {
        double yaw = entity.getLocation().getYaw();
        yaw = Math.toRadians(yaw);
        return BlockFace2DVector.getClosest(yaw);
    }

    /**
     * Get the highest non air block at a location using it's Y or MAX_HEIGHT
     *
     * @param loc The location to use
     * @param max whether to use the Y value in the location or max world height
     * @return the Y value of the highest non air block
     */
    public static int getYHighest(Location loc, boolean max) {
        Location location = loc.clone();
        if (max) {
            location.setY(location.getWorld().getMaxHeight() - 1);
        }
        return getYHighest(location);
    }

    /**
     * Returns the highest non air block below the Y coordinate given in the location
     *
     * @param location The location to use
     * @return int, the Y value of the highest non air block or 0
     */
    public static int getYHighest(Location location) {
        Location loc = location.clone();
        if (!loc.getChunk().isLoaded()) {
            loc.getChunk().load(true);
        }
        for (int y = loc.getBlockY(); y >= 0; y--) {
            loc.setY(y);
            if (loc.getBlock().getType() != Material.AIR) {
                loc.getChunk().unload(false, true);
                return y;
            }
        }
        loc.getChunk().unload(false, true);
        return 0;
    }

    /**
     * Sets Y value for the location to the highest non air block
     *
     * @param loc the location to modify
     */
    public static void setYHighest(Location loc) {
        loc.setY(getYHighest(loc, true));
    }

}
