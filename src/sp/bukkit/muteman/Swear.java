/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sp.bukkit.muteman;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

/**
 *
 * @author sp
 */
public class Swear {
    private static FileConfiguration config;
    private static File configFile;
    private static List swears;

    private static HashMap<String, Integer> data;
        public static void setupSwear(File dataFolder, MuteMan muteMan){
            configFile = new File(dataFolder,  "swear.yml");
            config = YamlConfiguration.loadConfiguration(configFile);
            if (config != null){
                // fuckin' a lot
                // old configurations system, come back!
                InputStream defaultConfigStream = muteMan.getResource("swear.yml");
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
                config.setDefaults(defaultConfig);
                config.options().copyDefaults(true);
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            data = new HashMap<String, Integer>();
            swears = config.getList("swears");
    }
    public static void check(PlayerChatEvent event){
        String player = event.getPlayer().getName();
        String string = (" "+event.getMessage()+" ").replace(".", " ").toLowerCase();
        Boolean sweared = false;
        int count = 1;
        for (int i = 0; i < swears.size(); i++){
            String swear = " "+swears.get(i).toString().toLowerCase()+" ";
            if (string.contains(swear)) {
                sweared = true;
                count = string.split(swear).length+1;
                break;
            }
        }
        if (!sweared) {
            if (config.getBoolean("settings.decease") && data.get(player) != null){
                int swCount = data.get(player);
                swCount--;
                if (swCount < 0) swCount = 0;
                data.remove(player);
                data.put(player, swCount);
            } else {
                data.remove(player);
            }
        } else {
            if (data.containsKey(player)) {
                int already = data.get(player);
                data.remove(player);
                data.put(player, already + count);
            } else {
                data.put(player, 1);
            }
        }
        if (data.containsKey(player)) {
            if (data.get(player) >= config.getInt("settings.damage.from")) {
                Player p = MuteMan.server.getPlayer(player);
                event.setCancelled(true);
                p.damage(config.getInt("settings.damage.damage"));
            }
            if (data.get(player) >= config.getInt("settings.mute.from")) {
                event.setCancelled(true);
                MuteManager.systemMute(player, Long.parseLong(Integer.toString(config.getInt("settings.mute.time"))));
            }
            if (data.get(player) <= config.getInt("settings.ignore")) {
                return;
            } else {
                event.getPlayer().sendMessage(Msg.$("dont-swear"));
            }
       }
    }
}
