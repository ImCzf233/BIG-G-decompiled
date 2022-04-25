package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.bigg.Client;
import me.bigg.event.StepEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Step extends Module {
   private final BoolValue smoothValue = new BoolValue("Smooth", "Step smooth", true);
   private final BoolValue packetValue = new BoolValue("Packet", "Packet step", true);
   private final NumValue heightValue = new NumValue("Height", "Just height", 1.0, 1.0, 5.0, 0.1);
   private final NumValue delayValue = new NumValue("Delay", "Just delay", 1.0, 1.0, 15.0, 0.1);
   private final TimerUtil timer = new TimerUtil();
   private boolean resetTimer;
   private int groundTicks;

   public Step() {
      super("Step", "Sorry", Category.Movement);
      this.addValues(new Value[]{this.heightValue, this.delayValue, this.smoothValue, this.packetValue});
   }

   public void onEnable() {
      super.onEnable();
      this.timer.reset();
      this.resetTimer = false;
      this.groundTicks = 0;
   }

   public void onDisable() {
      super.onDisable();
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel((Boolean)this.packetValue.getValue() ? "Packet" : "");
      if (event.isPre()) {
         if (PlayerUtil.isOnGround(0.01)) {
            ++this.groundTicks;
         } else {
            this.groundTicks = 0;
         }

         if (this.groundTicks > 20) {
            this.groundTicks = 20;
         }

      }
   }

   @EventTarget
   void onStep(StepEvent event) {
      if (this.resetTimer) {
         this.resetTimer = false;
         mc.timer.timerSpeed = 1.0F;
      }

      if (event.isPre() && this.canStep() && this.timer.hasPassed((long)((Number)this.delayValue.getValue()).intValue() * 100L)) {
         event.setStepHeight((Boolean)this.packetValue.getValue() ? Math.min(((Number)this.heightValue.getValue()).doubleValue(), 2.5) : ((Number)this.heightValue.getValue()).doubleValue());
      }

      if (event.isPost()) {
         Number realHeight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
         if (!(realHeight.doubleValue() >= 0.625)) {
            return;
         }

         this.timer.reset();
         if ((Boolean)this.smoothValue.getValue()) {
            mc.timer.timerSpeed = 0.4F - (realHeight.floatValue() >= 1.0F ? Math.abs(1.0F - realHeight.floatValue()) * 0.2F : 0.0F);
            if (mc.timer.timerSpeed <= 0.1F) {
               mc.timer.timerSpeed = 0.1F;
            }

            this.resetTimer = true;
         }

         if ((Boolean)this.packetValue.getValue()) {
            List offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
            double posX = mc.thePlayer.posX;
            double posZ = mc.thePlayer.posZ;
            double y = mc.thePlayer.posY;
            double first;
            if (realHeight.doubleValue() < 1.1) {
               first = 0.42;
               double second = 0.75;
               if (realHeight.doubleValue() != 1.0) {
                  first *= realHeight.doubleValue();
                  second *= realHeight.doubleValue();
                  if (first > 0.4213) {
                     first = 0.4213;
                  }

                  if (second > 0.78) {
                     second = 0.78;
                  }

                  if (second < 0.49) {
                     second = 0.49;
                  }
               }

               if (first == 0.42) {
                  first = 0.41999998688698;
               }

               mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
               if (y + second < y + realHeight.doubleValue()) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
               }
            } else if (realHeight.doubleValue() < 1.6) {
               Iterator var17 = offset.iterator();

               while(var17.hasNext()) {
                  first = (Double)var17.next();
                  y += first;
                  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
               }
            } else {
               double off;
               int var13;
               int var14;
               double[] var15;
               double[] heights;
               if (realHeight.doubleValue() < 2.1) {
                  heights = new double[]{0.4213, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
                  var15 = heights;
                  var14 = heights.length;

                  for(var13 = 0; var13 < var14; ++var13) {
                     off = var15[var13];
                     mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
                  }
               } else {
                  heights = new double[]{0.4213, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
                  var15 = heights;
                  var14 = heights.length;

                  for(var13 = 0; var13 < var14; ++var13) {
                     off = var15[var13];
                     mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
                  }
               }
            }
         }
      }

   }

   private boolean canStep() {
      return !Client.INSTANCE.getModuleManager().getModule("phase").isEnabled() && this.groundTicks > 3 && mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.pressed && !PlayerUtil.isInLiquid();
   }
}
