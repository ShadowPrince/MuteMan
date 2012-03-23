/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sp.bukkit.muteman;

import java.util.logging.Logger;

/**
 *
 * @author sp
 */
public class Log{
   private static Logger log = Logger.getLogger("minecraft");
   private static String prefix = "[MuteMan] ";
   public static void info(String str){
       log.info(prefix.concat(str));
   }
   public static void warning(String str){
       log.warning(prefix.concat(str));
   }
}
