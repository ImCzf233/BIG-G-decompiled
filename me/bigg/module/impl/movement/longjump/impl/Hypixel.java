package me.bigg.module.impl.movement.longjump.impl;

import me.bigg.event.UpdateEvent;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class Hypixel {
   private final Minecraft mc = Minecraft.getMinecraft();
   private int enableTime;

   public void onEnable() {
      this.enableTime = 0;
      if (this.mc.thePlayer.onGround) {
         this.mc.thePlayer.jump();
      }
   }

   public void onDisable() {
   }

   public void onUpdate(UpdateEvent event) {
      if (event.isPre() && !this.mc.thePlayer.onGround) {
         if (this.mc.thePlayer.motionY < -0.0) {
            EntityPlayerSP var10000 = this.mc.thePlayer;
            var10000.motionY *= Math.min(0.6 + (double)this.enableTime / 30.0, 0.96);
         }

         PlayerUtil.setSpeed(Math.max(PlayerUtil.getBaseMoveSpeed() * (2.199999999989 - (double)PlayerUtil.getSpeedEffect() * 0.03) - (double)(++this.enableTime) / 70.0, PlayerUtil.getBaseMoveSpeed()));
      }
   }
}
