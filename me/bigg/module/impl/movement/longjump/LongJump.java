package me.bigg.module.impl.movement.longjump;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.Client;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.movement.longjump.impl.Hypixel;
import me.bigg.module.impl.movement.longjump.impl.Redesky;
import me.bigg.module.impl.movement.longjump.impl.Vanilla;
import me.bigg.notification.Notification;
import me.bigg.util.PlayerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.Value;

public class LongJump extends Module {
   private final EnumValue modeValue = new EnumValue("Mode", "Longjump mode", LongJump.modeEnums.values());
   private final BoolValue toggleValue = new BoolValue("Toggle", "Toggle wen og", true);
   private int air;
   private final Vanilla vanilla = new Vanilla();
   private final Redesky redesky = new Redesky();
   private final Hypixel hypixel = new Hypixel();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$movement$longjump$LongJump$modeEnums;

   public LongJump() {
      super("Long Jump", "Jump higher & longer", Category.Movement);
      this.addValues(new Value[]{this.modeValue, this.toggleValue});
   }

   public void onEnable() {
      super.onEnable();
      Module[] clashes = new Module[]{Client.INSTANCE.getModuleManager().getModule("ScaffoldA"), Client.INSTANCE.getModuleManager().getModule("Speed"), Client.INSTANCE.getModuleManager().getModule("Fly")};
      int count = 0;
      Module[] var6 = clashes;
      int var5 = clashes.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         Module m = var6[var4];
         if (m.isEnabled()) {
            m.toggle();
            ++count;
         }
      }

      if (count > 0) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Disabled " + count + " clash modules"));
      }

      this.air = 0;
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$longjump$LongJump$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.vanilla.onEnable();
            break;
         case 2:
            this.redesky.onEnable();
            break;
         case 3:
            this.hypixel.onEnable();
      }

   }

   public void onDisable() {
      super.onDisable();
      PlayerUtil.setSpeed(0.0);
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name());
      if (!mc.thePlayer.onGround) {
         ++this.air;
      } else if (this.air > 3 && (Boolean)this.toggleValue.getValue()) {
         this.toggle();
      }

      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$longjump$LongJump$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.vanilla.onUpdate(event);
            break;
         case 2:
            this.redesky.onUpdate(event);
            break;
         case 3:
            this.hypixel.onUpdate(event);
      }

   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$movement$longjump$LongJump$modeEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$movement$longjump$LongJump$modeEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[LongJump.modeEnums.values().length];

         try {
            var0[LongJump.modeEnums.Hypixel.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[LongJump.modeEnums.Redesky.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[LongJump.modeEnums.Vanilla.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$movement$longjump$LongJump$modeEnums = var0;
         return var0;
      }
   }

   private static enum modeEnums {
      Vanilla,
      Redesky,
      Hypixel;
   }
}
