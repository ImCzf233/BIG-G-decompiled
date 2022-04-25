package me.bigg.module.impl.movement.longjump.impl;

import me.bigg.event.UpdateEvent;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;

public class Vanilla {
   private final Minecraft mc = Minecraft.getMinecraft();
   private int enableTime;

   public void onEnable() {
      this.enableTime = 0;
      if (this.mc.thePlayer.onGround) {
         this.mc.thePlayer.jump();
      }
   }

   public void onUpdate(UpdateEvent event) {
      if (event.isPre() && !this.mc.thePlayer.onGround) {
         ++this.enableTime;
         PlayerUtil.setSpeed(Math.max(PlayerUtil.getBaseMoveSpeed() * 4.0 - (double)this.enableTime / 30.0, PlayerUtil.getBaseMoveSpeed()));
      }
   }
}
