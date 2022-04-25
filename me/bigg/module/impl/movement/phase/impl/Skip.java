package me.bigg.module.impl.movement.phase.impl;

import me.bigg.event.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Skip {
   private final Minecraft mc = Minecraft.getMinecraft();

   public void onUpdate(UpdateEvent event) {
      if (event.isPost()) {
         double multiplier = 0.3;
         double mx = Math.cos(Math.toRadians((double)(this.mc.thePlayer.rotationYaw + 90.0F)));
         double mz = Math.sin(Math.toRadians((double)(this.mc.thePlayer.rotationYaw + 90.0F)));
         double x = (double)this.mc.thePlayer.movementInput.moveForward * multiplier * mx + (double)this.mc.thePlayer.movementInput.moveStrafe * multiplier * mz;
         double z = (double)this.mc.thePlayer.movementInput.moveForward * multiplier * mz - (double)this.mc.thePlayer.movementInput.moveStrafe * multiplier * mx;
         if (this.mc.thePlayer.isCollidedHorizontally) {
            EntityPlayerSP var10000 = this.mc.thePlayer;
            var10000.motionX *= 0.5;
            var10000 = this.mc.thePlayer;
            var10000.motionZ *= 0.5;
            double[] values = new double[]{-0.02500000037252903, -0.028571428997176036, -0.033333333830038704, -0.04000000059604645, -0.05000000074505806, -0.06666666766007741, -0.10000000149011612, 0.0, -0.20000000298023224, -0.04000000059604645, -0.033333333830038704, -0.028571428997176036, -0.02500000037252903};

            for(int j = 0; j < values.length; ++j) {
               this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + values[j], this.mc.thePlayer.posZ, false));
               double var10003 = this.mc.thePlayer.posX + x * (double)j;
               double var10005 = this.mc.thePlayer.posZ + z * (double)j;
               this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(var10003, this.mc.thePlayer.getEntityBoundingBox().minY, var10005, false));
            }

            this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, true));
            this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z);
            this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.getEntityBoundingBox().minY, this.mc.thePlayer.posZ, false));
         }
      }
   }
}
