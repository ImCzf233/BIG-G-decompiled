package me.bigg.module.impl.movement.phase.impl;

import me.bigg.event.UpdateEvent;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Vanilla {
   private final Minecraft mc = Minecraft.getMinecraft();

   public void onUpdate(UpdateEvent event) {
      if (event.isPost()) {
         double multiplier = 0.3;
         double mx = Math.cos(Math.toRadians((double)(this.mc.thePlayer.rotationYaw + 90.0F)));
         double mz = Math.sin(Math.toRadians((double)(this.mc.thePlayer.rotationYaw + 90.0F)));
         double x = (double)this.mc.thePlayer.movementInput.moveForward * multiplier * mx + (double)this.mc.thePlayer.movementInput.moveStrafe * multiplier * mz;
         double z = (double)this.mc.thePlayer.movementInput.moveForward * multiplier * mz - (double)this.mc.thePlayer.movementInput.moveStrafe * multiplier * mx;
         if (this.mc.thePlayer.isCollidedHorizontally && !this.mc.thePlayer.isOnLadder() && !PlayerUtil.InsideBlock()) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z, false));
            double posX2 = this.mc.thePlayer.posX;
            double posY2 = this.mc.thePlayer.posY;
            this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX2, posY2 - (PlayerUtil.isOnLiquid() ? 9000.0 : 0.1), this.mc.thePlayer.posZ, false));
            this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z);
         }

      }
   }
}
