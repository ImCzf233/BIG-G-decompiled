package me.bigg.module.impl.movement.fly.impl;

import me.bigg.event.MoveEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.impl.movement.TargetStrafe;
import me.bigg.module.impl.movement.fly.Fly;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;

public class Motion {
   private final Minecraft mc = Minecraft.getMinecraft();

   public void onDisable() {
      PlayerUtil.setSpeed(0.0);
      this.mc.thePlayer.motionY = 0.0;
   }

   public void onUpdate(UpdateEvent event) {
      if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
         this.mc.thePlayer.motionY = 2.0;
      } else if (this.mc.gameSettings.keyBindSneak.isKeyDown()) {
         this.mc.thePlayer.motionY = -2.0;
      } else {
         this.mc.thePlayer.motionY = 0.0;
      }

      if (!TargetStrafe.doingStrafe((MoveEvent)null, PlayerUtil.getBaseMoveSpeed() * ((Number)Fly.getSpeedValue().getValue()).doubleValue())) {
         PlayerUtil.setSpeed(PlayerUtil.getBaseMoveSpeed() * ((Number)Fly.getSpeedValue().getValue()).doubleValue());
      }

   }
}
