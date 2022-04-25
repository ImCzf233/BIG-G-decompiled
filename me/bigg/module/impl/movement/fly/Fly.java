package me.bigg.module.impl.movement.fly;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.Client;
import me.bigg.event.BBBEvent;
import me.bigg.event.MoveEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.movement.fly.impl.Creative;
import me.bigg.module.impl.movement.fly.impl.Hypixel;
import me.bigg.module.impl.movement.fly.impl.HypixelFast;
import me.bigg.module.impl.movement.fly.impl.HypixelFlag;
import me.bigg.module.impl.movement.fly.impl.HypixelZoom;
import me.bigg.module.impl.movement.fly.impl.Motion;
import me.bigg.module.impl.movement.fly.impl.Redesky;
import me.bigg.module.impl.movement.fly.impl.Walk;
import me.bigg.notification.Notification;
import me.bigg.util.PlayerUtil;
import me.bigg.util.RotationUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;

public class Fly extends Module {
   public final EnumValue modeValue = new EnumValue("Mode", "Fly function", Fly.modeEnums.values());
   private static final EnumValue liftModeValue = new EnumValue("Lift Mode", "Zoom lift mode", Fly.liftEnums.values());
   private static final NumValue speedValue = new NumValue("Motion Speed", "Motion fly", 15.0, 0.0, 20.0, 0.5);
   private static final NumValue timerSpeedValue = new NumValue("Timer Speed", "zoom timer speed", 1.0, 0.0, 5.0, 0.1);
   private static final NumValue boostDuraValue = new NumValue("Boost Dura", "zoom dura", 3.0, 0.0, 15.0, 1.0);
   private static final BoolValue noSoundValue = new BoolValue("No DMG Sound", "Zoom damage no sound", true);
   private static final BoolValue fullHeartValue = new BoolValue("DMG Full", "1.0 DMG", false);
   private static final BoolValue damageWaitValue = new BoolValue("DMG Wait", "Wait 4 dmg", false);
   private static final BoolValue bobbingValue = new BoolValue("Bobbing", "Cam bobbing", false);
   private final Walk walk = new Walk();
   private final Motion motion = new Motion();
   private final Creative creative = new Creative();
   private final Redesky redesky = new Redesky();
   private final Hypixel hypixel = new Hypixel();
   private final HypixelFast hypixelFast = new HypixelFast();
   private final HypixelZoom hypixelZoom = new HypixelZoom();
   private final HypixelFlag hypixelFlag = new HypixelFlag();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums;

   public Fly() {
      super("Fly", "Chinese KongFu", Category.Movement);
      this.addValues(new Value[]{this.modeValue, liftModeValue, speedValue, timerSpeedValue, boostDuraValue, noSoundValue, bobbingValue, fullHeartValue, damageWaitValue});
   }

   public void onEnable() {
      super.onEnable();
      Module[] clashes = new Module[]{Client.INSTANCE.getModuleManager().getModule("ScaffoldA"), Client.INSTANCE.getModuleManager().getModule("Speed"), Client.INSTANCE.getModuleManager().getModule("Longjump")};
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

      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 3:
            this.creative.onEnable();
            break;
         case 4:
            this.redesky.onEnable();
            break;
         case 5:
            this.hypixel.onEnable();
            break;
         case 6:
            this.hypixelFast.onEnable();
            break;
         case 7:
            this.hypixelZoom.onEnable();
            break;
         case 8:
            this.hypixelFlag.onEnable();
      }

   }

   public void onDisable() {
      super.onDisable();
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 2:
            this.motion.onDisable();
            break;
         case 3:
            this.creative.onDisable();
            break;
         case 4:
            this.redesky.onDisable();
         case 5:
         case 8:
         default:
            break;
         case 6:
            this.hypixelFast.onDisable();
            break;
         case 7:
            this.hypixelZoom.onDisable();
      }

   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name());
      if ((Boolean)bobbingValue.getValue() && PlayerUtil.MovementInput()) {
         mc.thePlayer.cameraYaw = 0.099F;
      }

      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.walk.onUpdate(event);
            break;
         case 2:
            this.motion.onUpdate(event);
            break;
         case 3:
            this.creative.onUpdate(event);
            break;
         case 4:
            this.redesky.onUpdate(event);
            break;
         case 5:
            this.hypixel.onUpdate(event);
            break;
         case 6:
            this.hypixelFast.onUpdate(event);
            break;
         case 7:
            this.hypixelZoom.onUpdate(event);
            break;
         case 8:
            this.hypixelFlag.onUpdate(event);
      }

   }

   @EventTarget
   void onMove(MoveEvent event) {
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 5:
            this.hypixel.onMove(event);
         case 6:
         default:
            break;
         case 7:
            this.hypixelZoom.onMove(event);
            break;
         case 8:
            this.hypixelFlag.onMove(event);
      }

   }

   @EventTarget
   void onPacket(PacketEvent event) {
      if (RotationUtil.serverRotate(event, RotationUtil.getEntityRotation(mc.thePlayer))) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Disabled " + this.getName() + " due to flag"));
         this.toggle();
      }

      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 4:
            this.redesky.onPacket(event);
            break;
         case 5:
            this.hypixel.onPacket(event);
      }

   }

   @EventTarget
   void onBBB(BBBEvent e) {
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 5:
            this.hypixel.onBBB(e);
         default:
      }
   }

   public static NumValue getSpeedValue() {
      return speedValue;
   }

   public static NumValue getTimerSpeedValue() {
      return timerSpeedValue;
   }

   public static NumValue getBoostDuraValue() {
      return boostDuraValue;
   }

   public static BoolValue getFullHeartValue() {
      return fullHeartValue;
   }

   public static BoolValue getDamageWaitValue() {
      return damageWaitValue;
   }

   public static BoolValue getNoSoundValue() {
      return noSoundValue;
   }

   public static EnumValue getLiftModeValue() {
      return liftModeValue;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Fly.modeEnums.values().length];

         try {
            var0[Fly.modeEnums.Creative.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var0[Fly.modeEnums.Hypixel.ordinal()] = 5;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[Fly.modeEnums.HypixelFast.ordinal()] = 6;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[Fly.modeEnums.HypixelFlag.ordinal()] = 8;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[Fly.modeEnums.HypixelZoom.ordinal()] = 7;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Fly.modeEnums.Motion.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Fly.modeEnums.Redesky.ordinal()] = 4;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Fly.modeEnums.Walk.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$movement$fly$Fly$modeEnums = var0;
         return var0;
      }
   }

   public static enum liftEnums {
      Instant,
      Reverse;
   }

   public static enum modeEnums {
      Walk,
      Motion,
      Creative,
      Redesky,
      Hypixel,
      HypixelFast,
      HypixelZoom,
      HypixelFlag;
   }
}
