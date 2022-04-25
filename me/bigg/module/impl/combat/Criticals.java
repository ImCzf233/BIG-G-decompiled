package me.bigg.module.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.AttackEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.StepEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.ChatUtil;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

public class Criticals extends Module {
   private final TimerUtil timer = new TimerUtil();
   private final TimerUtil prevent = new TimerUtil();
   private final EnumValue modeValue = new EnumValue("Mode", "Bypass modes", Criticals.modeEnums.values());
   private final NumValue hurtTimeValue = new NumValue("Hurt Time", "Crit active hurt time", 15.0, 0.0, 20.0, 1.0);
   private final NumValue delayValue = new NumValue("Delay", "Crit active delay", 3.0, 0.5, 10.0, 0.5);
   private int ticks;
   private boolean attacked;
   private int groundTicks;

   public Criticals() {
      super("Criticals", "Every attack with a crit", Category.Combat);
      this.addValues(new Value[]{this.modeValue, this.delayValue, this.hurtTimeValue});
   }

   public void onEnable() {
      super.onEnable();
      this.timer.reset();
      this.prevent.reset();
      this.attacked = false;
      this.ticks = 0;
      this.groundTicks = 0;
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name());
      if (PlayerUtil.isOnGround(0.01)) {
         ++this.groundTicks;
      } else {
         this.groundTicks = 0;
      }

      if (this.groundTicks > 20) {
         this.groundTicks = 20;
      }

      if (event.isPre() && this.modeValue.getMode() == Criticals.modeEnums.WatchdogPacket && this.attacked) {
         if (mc.thePlayer.onGround) {
            ++this.ticks;
            switch (this.ticks) {
               case 1:
                  event.setY(event.getY() + 0.0625 + Math.random() / 100.0);
                  event.setOnGround(false);
                  break;
               case 2:
                  event.setY(event.getY() + 0.03125 + Math.random() / 100.0);
                  event.setOnGround(false);
                  break;
               case 3:
                  this.attacked = false;
                  this.ticks = 0;
            }
         } else {
            this.attacked = false;
            this.ticks = 0;
         }
      }

   }

   @EventTarget
   void onStep(StepEvent event) {
      if (!event.isPre()) {
         this.prevent.reset();
      }

   }

   @EventTarget
   void onPacket(PacketEvent event) {
   }

   @EventTarget
   void onAttack(AttackEvent event) {
      boolean canCrit = this.groundTicks > 3 && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock().isFullBlock() && !PlayerUtil.isInLiquid() && !PlayerUtil.isOnLiquid() && !mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null && !PlayerUtil.isOnGround(-2.0);
      if (event.isPre() && canCrit && event.getEntity().hurtResistantTime <= ((Number)this.hurtTimeValue.getValue()).intValue() && this.prevent.hasPassed(300L) && this.timer.hasPassed((long)((Number)this.delayValue.getValue()).intValue() * 100L)) {
         ChatUtil.debug("trying crit");
         this.doVisionCrit(event.getEntity());
         this.doJumpCrit();
         this.doHopCrit();
         this.attacked = true;
         this.timer.reset();
      }

   }

   private void doVisionCrit(Entity target) {
      if (this.modeValue.getMode() == Criticals.modeEnums.Hypixel) {
         mc.thePlayer.onCriticalHit(target);
      }
   }

   private void doJumpCrit() {
      if (this.modeValue.getMode() == Criticals.modeEnums.Jump) {
         mc.thePlayer.motionY = 0.41999998688697815;
      }
   }

   private void doHopCrit() {
      if (this.modeValue.getMode() == Criticals.modeEnums.Hop) {
         mc.thePlayer.motionY = 0.1;
         mc.thePlayer.fallDistance = 0.1F;
         mc.thePlayer.onGround = false;
      }
   }

   private static enum modeEnums {
      Hypixel,
      WatchdogPacket,
      Hop,
      Jump;
   }
}
