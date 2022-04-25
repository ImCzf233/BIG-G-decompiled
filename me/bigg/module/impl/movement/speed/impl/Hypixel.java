package me.bigg.module.impl.movement.speed.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import me.bigg.event.MoveEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.impl.movement.TargetStrafe;
import me.bigg.util.PlayerUtil;
import net.minecraft.client.Minecraft;

public class Hypixel {
   private final Minecraft mc = Minecraft.getMinecraft();
   private double lastDist;
   private double moveSpeed;
   private int stage;

   public void onEnable() {
      this.lastDist = 0.0;
      this.moveSpeed = 0.0;
      this.stage = 0;
   }

   public void onDisable() {
   }

   public void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         this.lastDist = PlayerUtil.getLastDist();
      }

      if (event.isPre() && PlayerUtil.MovementInput() && PlayerUtil.getLastDist() > 0.0) {
         List bypassOffset = Arrays.asList(0.125, 0.25, 0.375, 0.625, 0.75, 0.015625, 0.5, 0.0625, 0.875, 0.1875);
         double d3 = event.getY() % 1.0;
         bypassOffset.sort(Comparator.comparingDouble((PreY) -> {
            return Math.abs(PreY - d3);
         }));
         double acc = event.getY() - d3 + (Double)bypassOffset.get(0);
         if (Math.abs((Double)bypassOffset.get(0) - d3) < 0.005) {
            event.setY(acc);
            if (this.mc.thePlayer.ticksExisted % 2 == 0 || this.stage == 0) {
               event.setOnGround(true);
            }
         } else {
            List BypassOffset2 = Arrays.asList(0.715, 0.945, 0.09, 0.155, 0.14, 0.045, 0.63, 0.31);
            double d3_ = event.getY() % 1.0;
            BypassOffset2.sort(Comparator.comparingDouble((PreY) -> {
               return Math.abs(PreY - d3_);
            }));
            acc = event.getY() - d3_ + (Double)BypassOffset2.get(0);
            if (Math.abs((Double)BypassOffset2.get(0) - d3_) < 0.005) {
               event.setY(acc);
            }
         }
      }

   }

   public void onMove(MoveEvent event) {
      if (PlayerUtil.MovementInput() && !PlayerUtil.isInLiquid() && !PlayerUtil.isOnLiquid()) {
         label51: {
            switch (this.stage) {
               case 2:
                  if (this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedVertically) {
                     event.setY(this.mc.thePlayer.motionY = 0.42 + (double)PlayerUtil.getJumpEffect() * 0.1);
                     this.moveSpeed = PlayerUtil.getBaseMoveSpeed() * 1.245;
                  }
                  break label51;
               case 3:
                  double difference = 0.87 * (this.lastDist - PlayerUtil.getBaseMoveSpeed());
                  this.moveSpeed = this.lastDist - difference;
                  break label51;
            }

            if (PlayerUtil.isOnGround(-this.mc.thePlayer.motionY) || this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.onGround) {
               this.stage = 1;
            }

            this.moveSpeed = this.lastDist - this.lastDist / 88.8;
         }

         if (this.mc.thePlayer.onGround) {
            event.setY(this.mc.thePlayer.motionY = 0.318 + (double)PlayerUtil.getJumpEffect() * 0.1);
         }

         this.moveSpeed = Math.max(this.moveSpeed, PlayerUtil.getBaseMoveSpeed());
         ++this.stage;
         if (!TargetStrafe.doingStrafe(event, this.moveSpeed)) {
            PlayerUtil.setSpeed(event, this.moveSpeed);
         }
      } else {
         this.lastDist = 0.0;
         this.moveSpeed = 0.0;
         this.stage = 0;
      }

   }
}
