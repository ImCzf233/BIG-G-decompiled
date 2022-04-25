package me.bigg.module.impl.movement.fly.impl;

import java.util.Random;
import me.bigg.event.UpdateEvent;
import me.bigg.util.MathUtil;
import me.bigg.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class HypixelFast {
   private final Minecraft mc = Minecraft.getMinecraft();
   private final Walk airWalk = new Walk();
   private final TimerUtil timer = new TimerUtil();
   private double bypass;
   private int counter;

   public void onEnable() {
      this.bypass = 0.0;
      this.counter = 0;
      this.timer.reset();
      if (this.mc.thePlayer.onGround) {
         this.mc.thePlayer.jump();
         EntityPlayerSP var10000 = this.mc.thePlayer;
         var10000.posY += 0.42 + (double)MathUtil.getRandom().nextFloat() * 1.0E-7;
      }
   }

   public void onDisable() {
      this.mc.timer.timerSpeed = 1.0F;
   }

   public void onUpdate(UpdateEvent event) {
      this.mc.timer.timerSpeed = (float)(this.timer.hasPassed(800L) ? 1 : 2);
      this.airWalk.onUpdate(event);
      if (event.isPre()) {
         if (++this.counter == 1) {
            this.bypass = 3.25E-4 + MathUtil.getRandom(1.0999E-5, (double)(new Random()).nextFloat() * 1.0E-6);
         } else if (this.counter == 2) {
            this.bypass *= -1.0;
            this.counter = 0;
         }

         event.setY(this.mc.thePlayer.getEntityBoundingBox().minY + this.bypass);
      }
   }
}
