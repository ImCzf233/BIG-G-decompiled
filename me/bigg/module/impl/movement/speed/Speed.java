package me.bigg.module.impl.movement.speed;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.Client;
import me.bigg.event.MoveEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.movement.speed.impl.Bhop;
import me.bigg.module.impl.movement.speed.impl.Hypixel;
import me.bigg.notification.Notification;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;

public class Speed extends Module {
   private final EnumValue modeValue = new EnumValue("Mode", "Bypass mode", Speed.modeEnums.values());
   private static final NumValue dmgBoostValue = new NumValue("DMG Boost", "Boost wen dmg", 1.3, 0.0, 3.0, 0.1);
   public static BoolValue yboost = new BoolValue("Height+", "e", true);
   private final Bhop bhop = new Bhop();
   private final Hypixel hypixel = new Hypixel();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums;

   public Speed() {
      super("Speed", "ZOOM ZOOM ZOOM!", Category.Movement);
      this.setKeybinding(48);
      this.addValues(new Value[]{this.modeValue, dmgBoostValue, yboost});
   }

   public void onEnable() {
      super.onEnable();
      Module[] clashes = new Module[]{Client.INSTANCE.getModuleManager().getModule("ScaffoldA"), Client.INSTANCE.getModuleManager().getModule("Fly"), Client.INSTANCE.getModuleManager().getModule("Longjump")};
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

      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.bhop.onEnable();
            break;
         case 2:
            this.hypixel.onEnable();
      }

   }

   public void onDisable() {
      super.onDisable();
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.bhop.onDisable();
            break;
         case 2:
            this.hypixel.onDisable();
      }

      mc.timer.timerSpeed = 1.0F;
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name());
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.bhop.onUpdate(event);
            break;
         case 2:
            this.hypixel.onUpdate(event);
      }

   }

   @EventTarget
   void onMove(MoveEvent event) {
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.bhop.onMove(event);
            break;
         case 2:
            this.hypixel.onMove(event);
      }

   }

   @EventTarget
   void onPacket(PacketEvent event) {
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Speed.modeEnums.values().length];

         try {
            var0[Speed.modeEnums.Bhop.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Speed.modeEnums.Hypixel.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$movement$speed$Speed$modeEnums = var0;
         return var0;
      }
   }

   private static enum modeEnums {
      Bhop,
      Hypixel;
   }
}
