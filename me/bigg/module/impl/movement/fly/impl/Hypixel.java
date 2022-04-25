package me.bigg.module.impl.movement.fly.impl;

import me.bigg.Client;
import me.bigg.event.BBBEvent;
import me.bigg.event.MoveEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;

public class Hypixel {
   private final Minecraft mc = Minecraft.getMinecraft();
   double start_x;
   double start_y;
   double start_z;
   int state;

   public void onEnable() {
      if (this.mc.thePlayer.onGround) {
         this.mc.thePlayer.motionY = 0.05;
      } else {
         Client.INSTANCE.getModuleManager().getModule("Fly").toggle();
      }

      this.state = 0;
      this.start_x = this.mc.thePlayer.posX;
      this.start_y = this.mc.thePlayer.posY;
      this.start_z = this.mc.thePlayer.posZ;
   }

   public void onPacket(PacketEvent event) {
      if (event.isOutGoing()) {
         if (event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition && this.state == 1) {
            ((C03PacketPlayer.C04PacketPlayerPosition)event.getPacket()).onGround = false;
         }

         if (event.getPacket() instanceof C03PacketPlayer && this.state == 1) {
            ((C03PacketPlayer)event.getPacket()).onGround = false;
         }

         if (event.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook && this.state == 1) {
            ((C03PacketPlayer.C05PacketPlayerLook)event.getPacket()).onGround = false;
         }

         if (event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook && this.state == 1) {
            ((C03PacketPlayer.C06PacketPlayerPosLook)event.getPacket()).onGround = false;
         }
      } else if (event.isInComing() && event.getPacket() instanceof S08PacketPlayerPosLook && this.state == 0) {
         this.state = 1;
      }

   }

   public void onMove(MoveEvent e) {
      if (this.state == 0) {
         e.setX(0.0);
         e.setZ(0.0);
      } else {
         PlayerUtil.setSpeed(PlayerUtil.getBaseMoveSpeed());
      }

   }

   public void onBBB(BBBEvent e) {
      double x = (double)e.getPos().getX();
      double y = (double)e.getPos().getY();
      double z = (double)e.getPos().getZ();
      switch (this.state) {
         case 0:
            if (y >= this.start_y - 1.0) {
               e.setBoundingBox(AxisAlignedBB.fromBounds(15.0, 1.0, 15.0, -15.0, -1.0, -15.0).offset(x, this.start_y + 1.0, z));
            }

            return;
         case 1:
            e.setBoundingBox(AxisAlignedBB.fromBounds(15.0, 1.0, 15.0, -15.0, -1.0, -15.0).offset(x, y + 0.05, z));
            return;
         default:
      }
   }

   public void onUpdate(UpdateEvent event) {
      if (event.isPre() && this.state == 0) {
         this.mc.thePlayer.posY = this.start_y;
      }

   }
}
