package me.bigg.notification;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class Lagback extends Module {
   private BlockPos lastGround;
   private double lastBouncePosY;
   private final TimerUtil timer = new TimerUtil();
   private final TimerUtil timerLagBack = new TimerUtil();
   private final EnumValue modeValue = new EnumValue("Mode", "Lag back mode", Lagback.modeEnums.values());
   private final BoolValue voidOnlyValue = new BoolValue("Void Only", "Detect blocks under", true);
   private final NumValue distanceValue = new NumValue("Distance", "Active distance", 7.0, 3.0, 15.0, 0.5);
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$notification$Lagback$modeEnums;

   public Lagback() {
      super("Lag Back", "Flag back when falling into void", Category.Movement);
      this.addValues(new Value[]{this.modeValue, this.distanceValue, this.voidOnlyValue});
   }

   public void onEnable() {
      super.onEnable();
      this.timer.reset();
      this.timerLagBack.reset();
      this.lastBouncePosY = 0.0;
      this.lastGround = mc.thePlayer.getPosition().add(0.0, ((Number)this.distanceValue.getValue()).doubleValue(), 0.0);
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name() + " " + ((Number)this.distanceValue.getValue()).doubleValue());
      if (event.isPre()) {
         boolean checks = !PlayerUtil.isBlockUnder() || !(Boolean)this.voidOnlyValue.getValue();
         if (mc.thePlayer.onGround) {
            if (mc.thePlayer.ticksExisted % 30 == 0) {
               this.lastGround = mc.thePlayer.getPosition();
            }

            this.lastBouncePosY = 0.0;
         }

         if (checks && !((double)mc.thePlayer.fallDistance <= ((Number)this.distanceValue.getValue()).doubleValue()) && this.timerLagBack.hasPassed(500L) && (!(mc.thePlayer.posY > this.lastBouncePosY - 0.5) || this.lastBouncePosY == 0.0)) {
            switch ($SWITCH_TABLE$me$bigg$notification$Lagback$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
               case 1:
                  event.setX((double)this.lastGround.getX());
                  event.setY((double)this.lastGround.getY());
                  event.setZ((double)this.lastGround.getZ());
                  this.timerLagBack.reset();
                  break;
               case 2:
                  mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 999.0, mc.thePlayer.posZ, false));
                  mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                  mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                  break;
               case 3:
                  PlayerUtil.setSpeed(0.0);
                  mc.thePlayer.motionY = 0.4001999986886975 * (double)(((Number)this.distanceValue.getValue()).intValue() / 2);
                  this.lastBouncePosY = mc.thePlayer.posY;
                  mc.thePlayer.fallDistance = 0.0F;
                  this.timerLagBack.reset();
                  break;
               case 4:
                  mc.thePlayer.setPosition((double)this.lastGround.getX(), (double)this.lastGround.getY(), (double)this.lastGround.getZ());
                  this.timerLagBack.reset();
            }

         }
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$notification$Lagback$modeEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$notification$Lagback$modeEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Lagback.modeEnums.values().length];

         try {
            var0[Lagback.modeEnums.Bounce.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Lagback.modeEnums.Packet.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Lagback.modeEnums.Position.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Lagback.modeEnums.Update.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$notification$Lagback$modeEnums = var0;
         return var0;
      }
   }

   private static enum modeEnums {
      Update,
      Packet,
      Bounce,
      Position;
   }
}
