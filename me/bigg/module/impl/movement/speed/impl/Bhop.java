package me.bigg.module.impl.movement.speed.impl;

import me.bigg.event.MoveEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.impl.movement.speed.Speed;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;

public class Bhop {
   private final Minecraft mc = Minecraft.getMinecraft();
   private double lastDist;
   private double moveSpeed;
   private int stage;
   private boolean legitJump = false;

   public void onEnable() {
      this.legitJump = true;
   }

   public void onDisable() {
      this.mc.timer.timerSpeed = 1.0F;
      if ((Boolean)Speed.yboost.getValue()) {
         this.mc.gameSettings.keyBindJump.pressed = false;
      }

   }

   public void onUpdate(UpdateEvent event) {
      this.mc.thePlayer.speedInAir = 0.02F;
      if (this.mc.thePlayer.onGround) {
         PlayerUtil.doStrafe(PlayerUtil.getBaseMoveSpeed() - Math.random() / 100.0);
         if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            PlayerUtil.doStrafe(Math.hypot(this.mc.thePlayer.motionX, this.mc.thePlayer.motionZ) * 1.34 - Math.random() / 400.0);
         }

         this.mc.thePlayer.jump();
      } else if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
         this.mc.thePlayer.speedInAir = (float)(0.029999999329447746 - Math.random() / 400.0);
      }

      double motionZ = this.mc.thePlayer.motionZ;
      double motionX = this.mc.thePlayer.motionX;
      PlayerUtil.doStrafe(Math.hypot(this.mc.thePlayer.motionX, this.mc.thePlayer.motionZ));
      if ((double)this.mc.thePlayer.speedInAir > 0.02) {
         this.mc.thePlayer.motionZ = (motionZ + this.mc.thePlayer.motionZ * 5.0) / 6.0;
         this.mc.thePlayer.motionX = (motionX + this.mc.thePlayer.motionX * 5.0) / 6.0;
      } else {
         this.mc.thePlayer.motionZ = (motionZ + this.mc.thePlayer.motionZ * 7.0) / 8.0;
         this.mc.thePlayer.motionX = (motionX + this.mc.thePlayer.motionX * 7.0) / 8.0;
      }

      if (this.mc.thePlayer.fallDistance > 0.0F) {
         EntityPlayerSP var10000 = this.mc.thePlayer;
         var10000.motionY += 0.014000000432133675 - Math.random() / 100.0;
      }

   }

   public void onMove(MoveEvent event) {
   }
}
