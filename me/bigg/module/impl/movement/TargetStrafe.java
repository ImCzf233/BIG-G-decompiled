package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.event.MoveEvent;
import me.bigg.event.RenderEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.combat.AntiBot;
import me.bigg.module.impl.combat.KillAura;
import me.bigg.util.PlayerUtil;
import me.bigg.util.RotationUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class TargetStrafe extends Module {
   private static int myStrafe = -1;
   private static boolean working;
   private static final BoolValue keepRangeValue = new BoolValue("Keep Range", "Keep your range to target", true);
   private static final BoolValue renderValue = new BoolValue("Render", "Render circle", false);
   private static final BoolValue manualValue = new BoolValue("Manual", "Work with your move", false);
   private static final BoolValue pressValue = new BoolValue("Press", "Press space", true);
   private static final BoolValue adaptValue = new BoolValue("Adapt", "Handle situations", true);
   private static final NumValue rangeValue = new NumValue("Range", "Strafe range", 2.0, 0.5, 4.5, 0.1);
   private static final NumValue closeRangeValue = new NumValue("Close Range", "Make you more close with target", 0.5, 0.0, 1.0, 0.1);

   public TargetStrafe() {
      super("Target Strafe", "Auto strafe around target", Category.Movement);
      this.addValues(new Value[]{closeRangeValue, rangeValue, keepRangeValue, renderValue, manualValue, pressValue, adaptValue});
   }

   public void onEnable() {
      super.onEnable();
      myStrafe = -1;
      working = false;
   }

   @EventTarget(4)
   void onRender(RenderEvent event) {
      if ((Boolean)renderValue.getValue()) {
         Iterator var3 = mc.theWorld.playerEntities.iterator();

         while(true) {
            EntityPlayer player;
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return;
                     }

                     player = (EntityPlayer)var3.next();
                  } while(AntiBot.isBot(player));
               } while(player instanceof EntityPlayerSP);
            } while(mc.thePlayer.getDistanceToEntity(player) > ((Number)rangeValue.getValue()).floatValue() + 2.0F);

            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GL11.glEnable(2881);
            GL11.glLineWidth(2.0F);
            GL11.glBegin(3);
            float r = 0.003921569F * (float)(working && player == KillAura.getTarget() ? Color.GREEN.getRed() : Color.WHITE.getRed());
            float g = 0.003921569F * (float)(working && player == KillAura.getTarget() ? Color.GREEN.getGreen() : Color.WHITE.getGreen());
            float b = 0.003921569F * (float)(working && player == KillAura.getTarget() ? Color.GREEN.getBlue() : Color.WHITE.getBlue());

            for(int i = 0; i <= 90; ++i) {
               GL11.glColor3f(r, g, b);
               GL11.glVertex3d(x + (double)((Number)rangeValue.getValue()).floatValue() * Math.cos((double)i * 6.283185307179586 / 45.0), y, z + (double)((Number)rangeValue.getValue()).floatValue() * Math.sin((double)i * 6.283185307179586 / 45.0));
            }

            GL11.glEnd();
            GL11.glDisable(2881);
            GL11.glDisable(2848);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
         }
      }
   }

   public static boolean doingStrafe(MoveEvent event, double moveSpeed) {
      working = false;
      if (!canStrafe()) {
         return false;
      } else {
         float pseudoForward = ((Number)rangeValue.getValue()).floatValue();
         float pseudoStrafe = 1.0F;
         float yaw = RotationUtil.getRotation(KillAura.getTarget())[0];
         float forward = pseudoForward;
         float strafe = (Boolean)manualValue.getValue() && mc.thePlayer.moveStrafing != 0.0F ? pseudoStrafe * mc.thePlayer.moveStrafing * (float)myStrafe : (float)myStrafe;
         float strafe2 = 0.0F;
         check();
         if ((double)pseudoForward != 0.0) {
            if ((double)strafe > 0.0) {
               if ((Boolean)keepRangeValue.getValue() && getEnemyDistance() < (double)((Number)rangeValue.getValue()).floatValue() && !mc.thePlayer.isCollidedHorizontally && !checkVoid(KillAura.getTarget())) {
                  yaw += pseudoForward > 0.0F ? -canSize() : canSize();
               }

               strafe2 += pseudoForward > 0.0F ? -45.0F / getAlgorithm() : 45.0F / getAlgorithm();
            } else if ((double)strafe < 0.0) {
               if ((Boolean)keepRangeValue.getValue() && getEnemyDistance() < (double)((Number)rangeValue.getValue()).floatValue() && !mc.thePlayer.isCollidedHorizontally && !checkVoid(KillAura.getTarget())) {
                  yaw += pseudoForward > 0.0F ? canSize() : -canSize();
               }

               strafe2 += pseudoForward > 0.0F ? 45.0F / getAlgorithm() : -45.0F / getAlgorithm();
            }

            strafe = 0.0F;
            if ((double)pseudoForward > 0.0) {
               forward = 1.0F;
            } else if ((double)pseudoForward < 0.0) {
               forward = -1.0F;
            }
         }

         if ((double)strafe > 0.0) {
            strafe = 1.0F;
         } else if ((double)strafe < 0.0) {
            strafe = -1.0F;
         }

         double[] doSomeMath = new double[]{Math.cos(Math.toRadians((double)yaw + 90.0 + (double)strafe2)), Math.sin(Math.toRadians((double)yaw + 90.0 + (double)strafe2))};
         double[] asLast = new double[]{(double)forward * moveSpeed * doSomeMath[0] + (double)strafe * moveSpeed * doSomeMath[1], (double)forward * moveSpeed * doSomeMath[1] - (double)strafe * moveSpeed * doSomeMath[0]};
         if (event != null) {
            event.setX(asLast[0]);
            event.setZ(asLast[1]);
         } else {
            mc.thePlayer.motionX = asLast[0];
            mc.thePlayer.motionZ = asLast[1];
         }

         working = true;
         return true;
      }
   }

   private static boolean canStrafe() {
      return Client.INSTANCE.getModuleManager().getModule("targetstrafe").isEnabled() && PlayerUtil.MovementInput() && KillAura.getTarget() != null && (!(Boolean)pressValue.getValue() || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
   }

   private static float canSize() {
      return (float)(45.0 / getEnemyDistance());
   }

   private static double getEnemyDistance() {
      return mc.thePlayer.getDistance(KillAura.getTarget().posX, mc.thePlayer.posY, KillAura.getTarget().posZ) + (double)((Number)closeRangeValue.getValue()).floatValue();
   }

   private static float getAlgorithm() {
      return (float)Math.max(getEnemyDistance() - (double)((Number)rangeValue.getValue()).floatValue(), getEnemyDistance() - (getEnemyDistance() - (double)(((Number)rangeValue.getValue()).floatValue() / (((Number)rangeValue.getValue()).floatValue() * 2.0F))));
   }

   private static void check() {
      if (mc.thePlayer.isCollidedHorizontally || checkVoid(KillAura.getTarget())) {
         if (myStrafe < 2) {
            ++myStrafe;
         } else {
            myStrafe = -1;
         }
      }

      switch (myStrafe) {
         case 0:
            myStrafe = 1;
         case 1:
         default:
            break;
         case 2:
            myStrafe = -1;
      }

   }

   private static boolean checkVoid(EntityLivingBase entity) {
      for(int x = -1; x <= 0; ++x) {
         for(int z = -1; z <= 0; ++z) {
            if (isVoid(x, z, entity)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean isVoid(int x, int z, EntityLivingBase entity) {
      if (!Client.INSTANCE.getModuleManager().getModule("fly").isEnabled() && (Boolean)adaptValue.getValue()) {
         if (mc.thePlayer.posY < 0.0) {
            return true;
         } else {
            for(int off = 0; (double)off < entity.posY + 2.0; off += 2) {
               AxisAlignedBB bb = entity.getEntityBoundingBox().offset((double)x, (double)(-off), (double)z);
               if (!mc.theWorld.getCollidingBoundingBoxes(entity, bb).isEmpty()) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
