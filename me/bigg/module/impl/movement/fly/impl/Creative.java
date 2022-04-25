package me.bigg.module.impl.movement.fly.impl;

import me.bigg.event.UpdateEvent;
import net.minecraft.client.Minecraft;

public class Creative {
   private final Minecraft mc = Minecraft.getMinecraft();

   public void onEnable() {
      this.mc.thePlayer.capabilities.isFlying = true;
   }

   public void onDisable() {
      this.mc.thePlayer.capabilities.isFlying = false;
   }

   public void onUpdate(UpdateEvent event) {
      this.mc.thePlayer.capabilities.isFlying = true;
   }
}
