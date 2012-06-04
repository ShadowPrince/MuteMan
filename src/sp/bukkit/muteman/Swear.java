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
import java.util.Vector;

import jline.ANSIBuffer;
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
        data = new HashMap<String, Integer>();

        configFile = new File(dataFolder,  "swear.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        if (config != null){
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
        if ( (swears = config.getList("swears")) == null) {
            swears = new Vector(1);
        }
    }

    public static void check(PlayerChatEvent event){
        String player = event.getPlayer().getName();
        String string = event.getMessage().replace(".", " ").replace("_", " ").replace(",", " ").toLowerCase();

        int count = 0;
        for (String word : string.split(" ")) {
            char[] chars = word.toCharArray();
            word = chars[0] + "";
            for (int i = 1; i < chars.length; i++) {
                if (chars[i] == chars[i-1]) {
                    continue;
                }
                word += chars[i];
            }    

            Log.debug("Result word - " + word.toLowerCase());
            if (swears.contains(word.toLowerCase())) {
                // that word in swears-list!
                count++;
            }
        }

        if (count == 0) { // if player not swear in message - decease his swear-counter
            Log.debug("Not swear, decease.");
            if (config.getBoolean("settings.decease") && data.get(player) != null){
                data.remove(player);
                data.put(player, data.get(player) == 1 ? 0 : data.get(player) - 1);
            } else {
                data.remove(player);
            }
        } else { // if player swears
            Log.debug("Swear! " + count + " times.");
            if (data.containsKey(player)) {
                data.remove(player);
                data.put(player, data.get(player) + count);
            } else {
                data.put(player, 1);
            }
        }
        // punishment
        if (data.containsKey(player)) {
            if (data.get(player) >= config.getInt("settings.damage.from")) {
                // punishment - damage
                event.getPlayer().damage(config.getInt("settings.damage.damage"));

                // punishment received
                event.setCancelled(true);
            }
            if (data.get(player) >= config.getInt("settings.mute.from")) {
                // punishment - mute
                MuteManager.systemMute(player, Long.parseLong(Integer.toString(config.getInt("settings.mute.time"))));

                // punishment received
                event.setCancelled(true);
            }

            if (data.get(player) <= config.getInt("settings.ignore")) {
                // swear-count too low
                return;
            } else {
                // dont swear!
                event.getPlayer().sendMessage(Msg.$("dont-swear"));
            }
       }
    }
}
