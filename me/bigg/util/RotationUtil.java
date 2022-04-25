package me.bigg.util;

import me.bigg.event.PacketEvent;
import me.bigg.util.sub.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;

public class RotationUtil {
   private static final Minecraft mc = Minecraft.getMinecraft();

   public static float[] getRotation(EntityLivingBase ent) {
      double lower = (double)ent.getEyeHeight() * (!ent.isChild() && !(ent.getEntityBoundingBox().maxY - ent.getEntityBoundingBox().minY < 1.0) ? 0.5 : 2.0);
      double x = ent.posX + (ent.posX - ent.prevPosX);
      double z = ent.posZ + (ent.posZ - ent.prevPosZ);
      double y = ent.posY - lower;
      return getRotationFromPosition(x, y, z);
   }

   public static float[] getRotationFromPosition(double x, double y, double z) {
      double xDiff = x - mc.thePlayer.posX;
      double zDiff = z - mc.thePlayer.posZ;
      double yDiff = y - mc.thePlayer.posY;
      double dist = (double)MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
      float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0F;
      float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
      return new float[]{yaw, pitch};
   }

   public static float getYawDifference(float currentYaw, double targetX, double targetY, double targetZ) {
      double deltaX = targetX - mc.thePlayer.posX;
      double deltaY = targetY - mc.thePlayer.posY;
      double deltaZ = targetZ - mc.thePlayer.posZ;
      double yawToEntity = 0.0;
      double degrees = Math.toDegrees(Math.atan(deltaZ / deltaX));
      if (deltaZ < 0.0 && deltaX < 0.0) {
         if (deltaX != 0.0) {
            yawToEntity = 90.0 + degrees;
         }
      } else if (deltaZ < 0.0 && deltaX > 0.0) {
         if (deltaX != 0.0) {
            yawToEntity = -90.0 + degrees;
         }
      } else if (deltaZ != 0.0) {
         yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
      }

      return MathHelper.wrapAngleTo180_float(-(currentYaw - (float)yawToEntity));
   }

   public static float getPitchDifference(float currentPitch, double targetX, double targetY, double targetZ) {
      double deltaX = targetX - mc.thePlayer.posX;
      double deltaY = targetY - mc.thePlayer.posY;
      double deltaZ = targetZ - mc.thePlayer.posZ;
      double distanceXZ = (double)MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
      double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
      return -MathHelper.wrapAngleTo180_float(currentPitch - (float)pitchToEntity) - 2.5F;
   }

   public static float getYawDifference(float currentYaw, float targetYaw) {
      return ((currentYaw - targetYaw) % 360.0F + 540.0F) % 360.0F - 180.0F;
   }

   public static boolean serverRotate(PacketEvent event, Rotation customRotation) {
      Packet packet = event.getPacket();
      if (event.isInComing() && packet instanceof S08PacketPlayerPosLook) {
         S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)packet;
         s08PacketPlayerPosLook.yaw = customRotation.getYaw();
         s08PacketPlayerPosLook.pitch = customRotation.getPitch();
         event.setPacket(s08PacketPlayerPosLook);
         return true;
      } else {
         return false;
      }
   }

   public static Rotation getEntityRotation(Entity entityIn) {
      return new Rotation(entityIn);
   }
}
