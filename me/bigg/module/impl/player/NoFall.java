package me.bigg.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;

public class NoFall extends Module {
   private int tick;

   public NoFall() {
      super("No Fall", "Remove fall damages", Category.Player);
   }

   public void onEnable() {
      this.tick = 1;
      super.onEnable();
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         this.setLabel("Watchdog");
         if (PlayerUtil.isBlockUnder()) {
            if (!mc.thePlayer.onGround && (double)mc.thePlayer.fallDistance - (double)this.tick * 2.5 >= 0.0) {
               if (mc.thePlayer.ticksExisted > 150) {
                  event.setOnGround(true);
               }

               ++this.tick;
            } else if (mc.thePlayer.onGround) {
               this.tick = 1;
            }
         }
      }

   }
}
