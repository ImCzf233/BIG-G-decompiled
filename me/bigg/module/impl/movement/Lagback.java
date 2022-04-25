package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class Lagback extends Module {
   BlockPos lastGroundPos;
   TimerUtil time = new TimerUtil();
   TimerUtil timer2 = new TimerUtil();
   public NumValue fall = new NumValue("Distance", "dd", 8.0, 1.0, 10.0, 1.0);
   private BoolValue ncp = new BoolValue("NCP", "wda", false);
   private boolean gng;

   public Lagback() {
      super("Lag back", "Move when has gui", Category.Movement);
      this.addValues(new Value[]{this.fall, this.ncp});
   }

   public void goToGround() {
      double minY = mc.thePlayer.posY - (double)mc.thePlayer.fallDistance;
      if (!(minY <= 0.0)) {
         double y = mc.thePlayer.posY;

         while(y > minY) {
            y -= 9.9;
            if (y < minY) {
               y = minY;
            }

            new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true);
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
         }

         y = minY;

         while(y < mc.thePlayer.posY) {
            y += 9.9;
            if (y > mc.thePlayer.posY) {
               y = mc.thePlayer.posY;
            }

            new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, false);
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, false));
         }

      }
   }

   @EventTarget
   public void onPre(PacketEvent e) {
      if (e.isOutGoing()) {
         if (mc.thePlayer.fallDistance >= ((Double)this.fall.getValue()).floatValue() && !this.isBlockUnder() && (Boolean)this.ncp.getValue()) {
            if (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook || e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
               mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-4, mc.thePlayer.posZ, true));
               e.setCancelled(true);
               mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }

            if (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook || e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
               mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 0.0, mc.thePlayer.posZ, false));
               e.setCancelled(true);
               mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }
         }

      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent e) {
      if (mc.thePlayer.onGround || mc.thePlayer.ticksExisted < 5) {
         this.gng = true;
         this.setLabel((Boolean)this.ncp.getValue() ? "NCP" : "Watchdog");
      }

      if (mc.thePlayer.fallDistance >= ((Double)this.fall.getValue()).floatValue() && !this.isBlockUnder() && !(Boolean)this.ncp.getValue() && this.gng) {
         mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 11.0 + (double)mc.thePlayer.fallDistance, mc.thePlayer.posZ, false));
         mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 5.5 + (double)mc.thePlayer.fallDistance, mc.thePlayer.posZ, true));
         mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.25 + (double)mc.thePlayer.fallDistance, mc.thePlayer.posZ, false));
         mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 9.111 + (double)mc.thePlayer.fallDistance, mc.thePlayer.posZ, false));
         this.gng = false;
      }

   }

   private boolean isBlockUnder() {
      for(int i = (int)mc.thePlayer.posY; i > 0; --i) {
         BlockPos pos = new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ);
         if (!(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir)) {
            return true;
         }
      }

      return false;
   }
}
