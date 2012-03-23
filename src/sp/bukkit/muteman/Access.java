/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sp.bukkit.muteman;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 *
 * @author sp
 */
public class Access {
    private static PermissionManager pex;
    public static void setupPermissions() {
        Log.info("Now using BukkitPermissions!");
        try {
            pex = PermissionsEx.getPermissionManager();
        } catch(NoClassDefFoundError e){
            Log.info("PermissionsEx not found, mute-set-group and mute-add-nodes are not available!");
        }
    }
    public static boolean canMute(Player player, String postfix) {
        if (player == null) return true;
        else {
            if ((player.hasPermission("commandbook.mute."+postfix))||(player.hasPermission("muteman.mute."+postfix)))
                return true;
            else
                return false;
        } 
    }
    public static boolean canMute(Player player){
        if (player == null) return true;
        else {
            if (player.hasPermission("commandbook.mute") || (player.hasPermission("muteman.mute")))
                return true;
            else
                return false;
        }
    }
    public static boolean canGet(Player player){
        if (player == null) return true;
        else {
            if (player.hasPermission("commandbook.mute") || (player.hasPermission("muteman.get")))
                return true;
            else
                return false;
        }
    }
    public static boolean canReload(CommandSender sender){
        if (sender instanceof Player){
            Player p = (Player) sender;
            if ((p.hasPermission("commandbook.mute"))||(p.hasPermission("muteman.reload"))){
                return false;
            }
        }
        return true;
    }
    public static boolean canSwear(CommandSender sender){
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (p.hasPermission("muteman.swear")){
                return true;
            }
        }
        
        return false;
    } 
    public static void removeNode(String player, String node){
        if (player == null) return;
        else {
            if (pex != null)
            pex.getUser(player).removePermission(node);
        }
    }
    public static void removeGroup(String player, String group){
            if (pex != null)
            pex.getUser(player).removeGroup(pex.getGroup(group));           
    }
    public static void addGroup(String player, String group){
            if (pex != null)
            pex.getUser(player).addGroup(pex.getGroup(group));      
    }
    public static void addNode(String player, String node) {
        if (player == null) {
            return;
        } else {
            if (pex != null)
            pex.getUser(player).addPermission(node);
        }
    }
}
