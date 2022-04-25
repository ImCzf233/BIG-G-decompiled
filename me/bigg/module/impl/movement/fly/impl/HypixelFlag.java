package me.bigg.module.impl.movement.fly.impl;

import me.bigg.event.MoveEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class HypixelFlag {
   private final Minecraft mc = Minecraft.getMinecraft();
   private TimerUtil flytimer = new TimerUtil();

   public void onEnable() {
      this.flytimer.reset();
   }

   public void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         this.mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
         this.mc.thePlayer.motionY = 0.0;
         this.mc.thePlayer.motionX = 0.0;
         this.mc.thePlayer.motionZ = 0.0;
         double yaw = Math.toRadians((double)this.mc.thePlayer.rotationYaw);
         double x = -Math.sin(yaw) * 6.0;
         double z = Math.cos(yaw) * 6.0;
         if (this.flytimer.hasPassed(1400L)) {
            this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY - 2.0, this.mc.thePlayer.posZ + z);
            this.flytimer.reset();
         }

      }
   }

   public void onMove(MoveEvent event) {
      event.setX(0.0);
      event.setZ(0.0);
   }
}
