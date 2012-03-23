/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sp.bukkit.muteman;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author sp
 */
public class MuteManager {
    private static FileConfiguration config;
    private static File configFile;
    public static void setupMuteManager(File dataFolder){
        configFile = new File(MuteMan.dataFolder, "data.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        
    }
    public static void Punishment(Player player){
        if (MuteMan.config.getBoolean("notify.enabled", true)) {
            player.sendMessage(Msg.$("mute-notify"));
            if (MuteMan.config.getBoolean("notify.time", true)) {
                Long time = MuteManager.getTime(player.getName());
                if (time == -1) {
                    player.sendMessage(Msg.$("time-left").replace("%time%", Msg.$("ever")));
                } else {
                    player.sendMessage(Msg.$("time-left").replace("%time%", MuteManager.getTime(player.getName()).toString().concat(Msg.$("sec"))));
                }
            }
        }
        int damage = MuteMan.config.getInt("damage", 0);
        if (damage != 0) {
            player.sendMessage(Msg.$("damaged"));
            player.damage(damage);
        }
    } 
    public static boolean isMuted(String player){
        if (player == null) return false;
        else {
            Double mute = config.getDouble("mute.".concat(player).concat(".time"), 0);
            
            if (mute == -1){
                return true;
            } else if (mute == 0){
                deleteMute(player);
                return false;
            } else {
                mute *= 1000;
                Long start;
                try {
                    start = (Long) config.get("mute.".concat(player).concat(".start"));
                } catch (Exception e){
                    return true;
                }
                if (start == null) {
                    return true;
                }
                Long mutems = Long.parseLong(Integer.toString(mute.intValue()));
                if (mutems == null)
                    return false;
                
                mutems += start;
                if (System.currentTimeMillis() < mutems){
                    return true;
                } else {
                    deleteMute(player);
                    return false;
                }
                
            }
        }
    }
    public static Long getTime(String player){       
        if (player == null) return new Long(0);
        else {
            Double mute = config.getDouble("mute.".concat(player).concat(".time"), 0);
            if (mute == -1) {
                return new Long(-1);
            }
            mute *= 1000;
            Long start;
            start = (Long) config.get("mute.".concat(player).concat(".start"));
            
            if (start == null) {
                return new Long(-1);
            }
            Long mutems = Long.parseLong(Integer.toString(mute.intValue()));
            mutems += start;
            return (new Long(mutems - System.currentTimeMillis()) / 1000);
        }
    }
    public static void systemMute(String muted, Long time) {
        config.set("mute.".concat(muted).concat(".time"), time);
        config.set("mute.".concat(muted).concat(".start"), System.currentTimeMillis());
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (time == -1) {
            MuteMan.server.getPlayer(muted).sendMessage(Msg.$("you-are-muted").replace("%time%", Msg.$("ever")));
        } else {
            MuteMan.server.getPlayer(muted).sendMessage(Msg.$("you-are-muted").replace("%time%", MuteManager.getTime(MuteMan.server.getPlayer(muted).getName()).toString().concat(Msg.$("sec"))));
        }
    }
    public static void Mute(CommandSender sender, String muted, Long time){
        if (sender instanceof Player) {
            if (!Access.canMute((Player) sender)){
                sender.sendMessage(Msg.$("not-permitted"));
                return;
            }
            if (time == -1) {
                if (!Access.canMute((Player) sender, "forever")) {
                    sender.sendMessage(Msg.$("not-permitted"));
                    return;  
                }
            }
        }
        if (!isMuted(muted)) {
            config.set("mute.".concat(muted).concat(".time"), time);
            config.set("mute.".concat(muted).concat(".start"), System.currentTimeMillis());
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            sender.sendMessage(Msg.$("muted-success").replace("%player%", muted));
            if (time == -1) {
                MuteMan.server.getPlayer(muted).sendMessage(Msg.$("you-are-muted").replace("%time%", Msg.$("ever")));
            } else {
                MuteMan.server.getPlayer(muted).sendMessage(Msg.$("you-are-muted").replace("%time%", MuteManager.getTime(MuteMan.server.getPlayer(muted).getName()).toString().concat(Msg.$("sec"))));
            }
        } else {
            sender.sendMessage(Msg.$("muted-already").replace("%time%", getTime(muted).toString()).replace("-1", Msg.$("ever")));
        }
    }
    
    public static void unMute(CommandSender sender, String mutted){
        if (sender instanceof Player && Access.canMute((Player) sender)){
            deleteMute(mutted);
            sender.sendMessage(Msg.$("unmutted-success").replace("%player%", mutted));
        } else {
            sender.sendMessage(Msg.$("not-permitted"));
        }
    }
    public static void deleteMute(String muted){
        List perms = MuteMan.config.getStringList("add-permissions.list");
        for (int i = 0; i < perms.size(); i++) {
            Access.removeNode(muted, (String) perms.get(i));
        }
        if (MuteMan.config.getString("set-group", "") != "") {
            String group = MuteMan.config.getString("set-group", "");
            Access.removeGroup(muted, group);
        }
        config.set("mute.".concat(muted), null);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void getMute(CommandSender sender, String player) {
        if (Access.canGet((Player) sender) || !(sender instanceof Player)) {
            if (isMuted(player)) {
                Long time = getTime(player);
                if (time == -1) {
                    sender.sendMessage(Msg.$("getmute-already").replace("%time%", Msg.$("ever")).replace("%player%", player));
                } else {
                    sender.sendMessage(Msg.$("getmute-already").replace("%time%", time.toString().concat(Msg.$("sec"))).replace("%player%", player));
                }
            } else {
                sender.sendMessage(Msg.$("getmute-not").replace("%player%", player));
            }

        } else {
            sender.sendMessage(Msg.$("not-permitted"));
        }
    }
}
