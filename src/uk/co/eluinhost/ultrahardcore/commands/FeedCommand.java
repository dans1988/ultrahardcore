package uk.co.eluinhost.ultrahardcore.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;

public class FeedCommand implements UHCCommand {

    public static final float MAX_SATURATION = 5.0F;
    public static final int MAX_FOOD_LEVEL = 20;

    //TODO needs some abstraction
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
                             String[] args) {
        if ("feed".equals(command.getName())) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (p.hasPermission(PermissionNodes.FEED_SELF)) {
                        p.setFoodLevel(MAX_FOOD_LEVEL);
                        p.setExhaustion(0.0F);
                        p.setSaturation(MAX_SATURATION);
                        p.sendMessage(ChatColor.GOLD + "You fed yourself to full hunger");
                        ServerUtil.broadcastForPermission(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] Player " + p.getName() + " used a feed command to feed themselves", PermissionNodes.FEED_ANNOUNCE);
                    } else {
                        p.sendMessage(ChatColor.RED + "You don't have the permission to feed yourself (" + PermissionNodes.FEED_SELF + ")");
                    }
                    return true;
                } else {
                    sender.sendMessage("You can only use /feed to feed yourself as a player use '/feed <player>' to heal a specific player");
                    return true;
                }
            } else {
                if ("*".equalsIgnoreCase(args[0])) {
                    if (!sender.hasPermission(PermissionNodes.FEED_ALL)) {
                        sender.sendMessage(ChatColor.RED + "You don't have the permission to feed all players (" + PermissionNodes.FEED_ALL + ")");
                        return true;
                    }
                    for (Player p : sender.getServer().getOnlinePlayers()) {
                        p.setFoodLevel(MAX_FOOD_LEVEL);
                        p.setExhaustion(0);
                        p.setSaturation(MAX_SATURATION);
                        p.sendMessage(ChatColor.GOLD + "You were fed back to full hunger");
                    }
                    ServerUtil.broadcastForPermission(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] " +
                            (sender instanceof Player ? "Player " + sender.getName() : "Console") + " fed all players"
                            , PermissionNodes.FEED_ANNOUNCE);

                    return true;
                }
                Player p = sender.getServer().getPlayer(args[0]);
                if (p == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid player name " + args[0]);
                    return true;
                }
                if (!sender.hasPermission(PermissionNodes.FEED_OTHER)) {
                    sender.sendMessage(ChatColor.RED + "You don't have the permission to feed another player (" + PermissionNodes.FEED_OTHER + "). If you want to feed yourself and have permissions use '/feed' by itself");
                    return true;
                }
                p.setFoodLevel(MAX_FOOD_LEVEL);
                p.setExhaustion(0);
                p.setSaturation(MAX_SATURATION);
                p.sendMessage(ChatColor.GOLD + "You were fed to full hunger");
                ServerUtil.broadcastForPermission(String.valueOf(ChatColor.GRAY) + ChatColor.ITALIC + "[UHC] " +
                        (sender instanceof Player ? "Player " + sender.getName() : "Console") + " fed player " + p.getName()
                        , PermissionNodes.HEAL_ANNOUNCE);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> p = ServerUtil.getOnlinePlayers();
        p.add("*");
        return p;
    }

}
