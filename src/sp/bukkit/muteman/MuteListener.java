/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sp.bukkit.muteman;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author sp
 */
public class MuteListener implements Listener {
   /*
    * Smiles system, replace patterns to cymbols,
    * like ':)' to '⒀' (some unicode symbol defined in glyph_xx.png)
    * Replace ⒀ to smile-picture in your MC-client, and add that symbol in font.txt in server jar, and its works.
    * Only for that dudes, who hold mc-server with modified client, just like me.
    */
    public String prepareMessage(String message) {
        /*
         * Section 'smiles' in config.yml.
         * List of strings, like
         * :),:-),=)~⒀
         * Replaces :), :-) and =) to ⒀.
         */
        List smiles = MuteMan.config.getList("smiles");
        for (Object smileObject : smiles) {
            String[] smileData = ((String) smileObject).split("~");
            String[] smileReplace = smileData[0].split(",");
            for (String smilePattern : smileReplace) {
                message = message.replace(smilePattern, smileData[1]);
            }
        }

        /*
         * Solution for non-english servers,
         * replaces characters in other codings (windows =\) to unicode with pattern "rep_patt" in config.yml. Also, works like ruFix (known in ru-net)
         * Correct pattern:
         * ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ~АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯЁабвгдежзийклмнопрстуфхцчшщъыьэюяё
         * (russian)
         */
        List replace_pattern = MuteMan.config.getList("rep_patt");
        for (Object rpObject : replace_pattern) {
            String[] rpData = rpObject.toString().split("~");
            for (int i = 0; i < rpData[0].length(); i++) {
                message = message.replace(rpData[0].charAt(i), rpData[1].charAt(i));
            }

        }
        return message;
    }
    
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        // Apply smiles and coding fixes
        event.setMessage(prepareMessage(event.getMessage()));
        if (MuteManager.isMuted(player.getName())) {
            MuteManager.Punishment(player);
            event.setCancelled(true);
        } else {
            if (!Access.canSwear(event.getPlayer()))
                Swear.check(event);
        }
    }

    /*
     * Disable commands for poor muted users
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
        // Prepare message
        event.setMessage(prepareMessage(event.getMessage()));
        MuteManager.isMuted(event.getPlayer().getName());
        if (event.getMessage().trim().startsWith("/mute") || event.getMessage().trim().startsWith("/unmute")) {
            Player sender = event.getPlayer();
            String[] args = event.getMessage().split(" ");
            String cmd = args[0];
            if (cmd.equalsIgnoreCase("/mute")){
                if (args.length <= 2) {
                    sender.sendMessage(Msg.$("mute-usage"));
                    event.setCancelled(true);
                    return; 
                }
                MuteMan.commandMute(sender, args[1], args[2]);
                
            } else if (cmd.equalsIgnoreCase("/unmute")){
               if (args.length <= 1) {
                    sender.sendMessage(Msg.$("unmute-usage"));
                    event.setCancelled(true);
                    return; 
                }
                MuteMan.commandUnMute(sender, args[1]);
            }

            event.setCancelled(true);
        } else {
            if (!event.getMessage().trim().startsWith("/getmute") || (!event.getMessage().trim().startsWith("/mymute")) || (!event.getMessage().trim().startsWith("/unmute"))) {
                if (MuteMan.config.getBoolean("disable-commands", false)) {
                    if (MuteManager.isMuted(event.getPlayer().getName())){
                        MuteManager.Punishment(event.getPlayer());
                        event.setCancelled(true);
                    }
                }
            }
        }        
    }
}
