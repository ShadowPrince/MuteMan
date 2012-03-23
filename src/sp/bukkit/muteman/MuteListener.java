/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sp.bukkit.muteman;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author sp
 */
public class MuteListener implements Listener {
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (MuteManager.isMuted(player.getName())) {
            MuteManager.Punishment(player);
            event.setCancelled(true);
        } else {
            if (!Access.canSwear(event.getPlayer()));
            Swear.check(event);
        }
    }
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
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
