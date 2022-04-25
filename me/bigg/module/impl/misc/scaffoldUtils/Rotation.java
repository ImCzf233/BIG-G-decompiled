package me.bigg.module.impl.misc.scaffoldUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public final class Rotation {
   private float yaw;
   private float pitch;

   public Rotation() {
      this(0.0F, 0.0F);
   }

   public Rotation(float yaw, float pitch) {
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void fixedSensitivity(float sensitivity) {
      float f = sensitivity * 0.6F + 0.2F;
      float gcd = f * f * f * 1.2F;
      this.yaw -= this.yaw % gcd;
      this.pitch -= this.pitch % gcd;
   }

   public void toPlayer(EntityPlayerSP player) {
      this.fixedSensitivity(Minecraft.getMinecraft().gameSettings.mouseSensitivity);
      player.rotationYaw = this.yaw;
      player.rotationPitch = this.pitch;
   }
}
