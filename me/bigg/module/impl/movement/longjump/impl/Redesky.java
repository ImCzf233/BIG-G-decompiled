package me.bigg.module.impl.movement.longjump.impl;

import me.bigg.event.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Redesky {
   private final Minecraft mc = Minecraft.getMinecraft();

   public void onEnable() {
      if (this.mc.thePlayer.onGround) {
         this.mc.thePlayer.jump();
      }
   }

   public void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         this.mc.thePlayer.jumpMovementFactor = 0.12F;
         EntityPlayerSP var10000 = this.mc.thePlayer;
         var10000.motionY += 0.029999999329447746;
      }
   }
}
