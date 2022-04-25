package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.Client;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;

public class Strafe extends Module {
   public Strafe() {
      super("Strafe", "Move strafe", Category.Movement);
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (!Client.INSTANCE.getModuleManager().getModule("longjump").isEnabled() && !Client.INSTANCE.getModuleManager().getModule("speed").isEnabled() && !Client.INSTANCE.getModuleManager().getModule("fly").isEnabled()) {
         PlayerUtil.doStrafe();
      }
   }
}
