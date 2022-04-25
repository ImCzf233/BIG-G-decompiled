package me.bigg.module.impl.movement.fly.impl;

import java.util.Random;
import me.bigg.Client;
import me.bigg.event.MoveEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.impl.movement.TargetStrafe;
import me.bigg.module.impl.movement.fly.Fly;
import me.bigg.notification.Notification;
import me.bigg.util.MathUtil;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class HypixelZoom {
   private final Minecraft mc = Minecraft.getMinecraft();
   private final Walk airWalk = new Walk();
   private int counter;
   private int boostStage;
   private double moveSpeed;
   private double distance;
   private double start;
   private double bypass;
   private boolean falling;
   private boolean shouldSlow;
   private boolean damaged;
   private boolean canFly;
   private static boolean ableSound;
   private final TimerUtil timer = new TimerUtil();

   public void onEnable() {
      this.falling = this.mc.thePlayer.fallDistance > 2.0F;
      this.shouldSlow = !this.mc.thePlayer.onGround;
      this.canFly = false;
      this.damaged = false;
      ableSound = false;
      this.bypass = 0.0;
      this.boostStage = 0;
      this.moveSpeed = 0.0;
      this.distance = 0.0;
      this.counter = 0;
      this.start = this.mc.thePlayer.posY;
      this.timer.reset();
      if (!this.shouldSlow && !this.falling) {
         if (PlayerUtil.isOnGround(-2.0)) {
            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Blocks above on ur head!"));
            this.shouldSlow = true;
            this.damaged = true;
            this.canFly = true;
            return;
         }

         this.damagePlayer((Boolean)Fly.getFullHeartValue().getValue() ? 4.0125 : 3.0125, !(Boolean)Fly.getDamageWaitValue().getValue());
         if (!(Boolean)Fly.getDamageWaitValue().getValue()) {
            if (Fly.getLiftModeValue().getMode() == Fly.liftEnums.Instant) {
               this.canFly = true;
               this.mc.thePlayer.jump();
               EntityPlayerSP var10000 = this.mc.thePlayer;
               var10000.posY += 0.42 + MathUtil.getRandom().nextDouble() * 1.0E-10;
            } else if (Fly.getLiftModeValue().getMode() == Fly.liftEnums.Reverse) {
               this.mc.thePlayer.jump();
               this.mc.timer.timerSpeed = 0.45F;
            }

            this.damaged = true;
         }
      } else {
         this.canFly = true;
         this.damaged = true;
      }

   }

   public void onDisable() {
      this.mc.timer.timerSpeed = 1.0F;
   }

   public void onUpdate(UpdateEvent event) {
      if (!this.canFly) {
         this.timer.reset();
         if (Fly.getLiftModeValue().getMode() == Fly.liftEnums.Instant) {
            if ((Boolean)Fly.getDamageWaitValue().getValue() && !this.damaged && this.mc.thePlayer.hurtResistantTime >= 18) {
               this.damaged = true;
               this.canFly = true;
               this.mc.thePlayer.jump();
               EntityPlayerSP var10000 = this.mc.thePlayer;
               var10000.posY += 0.42 + MathUtil.getRandom().nextDouble() * 1.0E-10;
            }
         } else if (Fly.getLiftModeValue().getMode() == Fly.liftEnums.Reverse) {
            if ((Boolean)Fly.getDamageWaitValue().getValue() && !this.damaged && this.mc.thePlayer.hurtResistantTime >= 19) {
               this.damaged = true;
               this.mc.thePlayer.jump();
               this.mc.timer.timerSpeed = 0.45F;
            }

            double difference = this.mc.thePlayer.posY - this.start;
            if (difference >= 0.7531999805212024) {
               this.mc.thePlayer.motionY = -0.33319999363422426;
               this.mc.timer.timerSpeed = 1.0F;
               this.canFly = true;
            }
         }

      } else {
         if ((Boolean)Fly.getNoSoundValue().getValue() && !ableSound) {
            this.mc.thePlayer.hurtTime = 0;
         }

         if (!ableSound && this.mc.thePlayer.hurtResistantTime >= 15) {
            ableSound = true;
         }

         if (event.isPre()) {
            this.airWalk.onUpdate(event);
            this.distance = PlayerUtil.getLastDist();
            if (!this.falling && !this.shouldSlow) {
               if (++this.counter == 1) {
                  this.bypass = 3.25E-4 + MathUtil.getRandom(1.0999E-5, (double)(new Random()).nextFloat() * 1.0E-6);
               } else if (this.counter == 2) {
                  this.bypass *= -1.0;
                  this.counter = 0;
               }

               event.setY(this.mc.thePlayer.getEntityBoundingBox().minY + this.bypass);
               this.mc.timer.timerSpeed = this.timer.hasPassed((long)((Number)Fly.getBoostDuraValue().getValue()).intValue() * 100L) ? 1.0F : 1.0F + ((Number)Fly.getTimerSpeedValue().getValue()).floatValue();
            } else {
               this.timer.reset();
            }
         }
      }
   }

   public void onMove(MoveEvent event) {
      if (this.falling) {
         if (!TargetStrafe.doingStrafe(event, PlayerUtil.getBaseMoveSpeed() * (double)((Number)Fly.getSpeedValue().getValue()).intValue())) {
            PlayerUtil.setSpeed(event, PlayerUtil.getBaseMoveSpeed() * (double)((Number)Fly.getSpeedValue().getValue()).intValue());
         }

      } else if (!this.canFly) {
         if (!this.damaged) {
            PlayerUtil.setSpeed(event, 0.0);
         }

      } else {
         double potion = 1.0 + (this.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.2 * (double)(this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) : 0.0);
         double bypass = 0.29 * potion;
         switch (++this.boostStage) {
            case 1:
               this.moveSpeed = (this.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.56 : 2.04) * bypass;
               break;
            case 2:
               this.moveSpeed *= this.mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 2.34 : 2.44;
               break;
            case 3:
               this.moveSpeed = this.distance - (this.mc.thePlayer.ticksExisted % 3 == 0 ? 0.01 : 0.015) * (this.distance - bypass);
               break;
            default:
               this.moveSpeed = this.distance - this.distance / 159.8;
               if (this.boostStage > 15) {
                  this.boostStage = 15;
               }
         }

         this.moveSpeed = Math.max(this.moveSpeed, PlayerUtil.getBaseMoveSpeed());
         this.moveSpeed = this.shouldSlow ? PlayerUtil.getBaseMoveSpeed() : this.moveSpeed;
         if (!TargetStrafe.doingStrafe(event, this.moveSpeed)) {
            PlayerUtil.setSpeed(event, this.moveSpeed);
         }

      }
   }

   private void damagePlayer(double distance, boolean ground) {
      for(int i = 0; (double)i < distance / 0.06199995; ++i) {
         this.mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.06199998, this.mc.thePlayer.posZ, false));
         this.mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.05599998, this.mc.thePlayer.posZ, false));
         this.mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, false));
      }

      if (ground) {
         this.mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer(true));
      }

   }

   public static boolean isAbleSound() {
      return ableSound;
   }
}
