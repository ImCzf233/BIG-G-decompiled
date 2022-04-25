package me.bigg.module.impl.movement.fly.impl;

import me.bigg.event.UpdateEvent;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;

public class Walk {
   private final Minecraft mc = Minecraft.getMinecraft();

   public void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         this.mc.thePlayer.motionY = 0.0;
         PlayerUtil.setSpeed(PlayerUtil.getBaseMoveSpeed());
      }
   }
}
