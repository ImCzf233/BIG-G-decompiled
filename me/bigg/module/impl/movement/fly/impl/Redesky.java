package me.bigg.module.impl.movement.fly.impl;

import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class Redesky {
   private final Minecraft mc = Minecraft.getMinecraft();
   int sb;
   boolean add;
   ArrayList cnm = new ArrayList();

   public void onEnable() {
      this.sb = 0;
      this.cnm.clear();
      this.add = true;
   }

   public void onDisable() {
      if (!this.cnm.isEmpty()) {
         Iterator var2 = this.cnm.iterator();

         while(var2.hasNext()) {
            Packet p = (Packet)var2.next();
            this.mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
         }

         this.cnm.clear();
      }

      this.mc.timer.timerSpeed = 1.0F;
      PlayerUtil.setSpeed(0.0);
   }

   public void onUpdate(UpdateEvent event) {
      Block block = this.getBlockUnderPlayer(this.mc.thePlayer, 0.2);
      if (!PlayerUtil.isOnGround(1.0E-7) && !block.isFullBlock() && !(block instanceof BlockGlass)) {
         this.add = true;
         this.mc.thePlayer.setSprinting(false);
         this.mc.timer.timerSpeed = 0.4F;
         this.mc.thePlayer.cameraYaw = 0.099F;
         if (this.sb != 1) {
            this.mc.thePlayer.motionY = 0.0;
         }

         if (++this.sb == 1) {
            if (this.mc.thePlayer.onGround) {
               this.mc.thePlayer.jump();
            }
         } else if (this.sb <= 10 && this.sb >= 5) {
            PlayerUtil.setSpeed(3.0);
            if (this.sb >= 9) {
               this.sb = 1;
            }
         } else {
            PlayerUtil.setSpeed(0.0);
         }
      } else {
         this.mc.timer.timerSpeed = 1.0F;
         this.sb = 0;
      }

   }

   public void onPacket(PacketEvent event) {
      if (event.getPacket() instanceof C03PacketPlayer) {
         this.cnm.add(event.getPacket());
      }

   }

   private Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
      return this.mc.theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
   }
}
