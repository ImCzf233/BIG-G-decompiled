package me.bigg.module.impl.misc;

import me.bigg.Client;
import me.bigg.module.Category;
import me.bigg.module.Module;
import net.minecraft.entity.EntityLivingBase;

public class Teams extends Module {
   public Teams() {
      super("Teams", "Check players same team", Category.Misc);
   }

   public static boolean isOnSameTeam(EntityLivingBase entity) {
      if (Client.INSTANCE.getModuleManager().getModule("teams").isEnabled() && entity != mc.thePlayer) {
         String self = mc.thePlayer.getDisplayName().getUnformattedText();
         String target = entity.getDisplayName().getUnformattedText();
         if (self.startsWith("§")) {
            if (!target.contains("§")) {
               return true;
            } else {
               return self.length() > 2 && target.length() > 2 ? self.substring(0, 2).equals(target.substring(0, 2)) : false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
