package me.bigg.module.impl.misc.scaffoldUtils;

import java.util.Random;
import me.bigg.event.PacketEvent;
import me.bigg.event.TickEvent;
import me.bigg.event.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RotationUtil {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private static Random random = new Random();
   private static int keepLength;
   public static Rotation targetRotation;
   public static Rotation serverRotation = new Rotation(0.0F, 0.0F);
   public static boolean keepCurrentRotation = false;
   private static double x;
   private static double y;
   private static double z;

   static {
      x = random.nextDouble();
      y = random.nextDouble();
      z = random.nextDouble();
   }

   public static VecRotation faceBlock(BlockPos blockPos) {
      if (blockPos == null) {
         return null;
      } else {
         VecRotation vecRotation = null;

         for(double xSearch = 0.1; xSearch < 0.9; xSearch += 0.1) {
            for(double ySearch = 0.1; ySearch < 0.9; ySearch += 0.1) {
               for(double zSearch = 0.1; zSearch < 0.9; zSearch += 0.1) {
                  Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                  Vec3 posVec = (new Vec3(blockPos)).addVector(xSearch, ySearch, zSearch);
                  double diffX = posVec.xCoord - eyesPos.xCoord;
                  double diffY = posVec.yCoord - eyesPos.yCoord;
                  double diffZ = posVec.zCoord - eyesPos.zCoord;
                  double diffXZ = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
                  Rotation rotation = new Rotation(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)))));
                  Vec3 rotationVector = getVectorForRotation(rotation);
                  Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4.0, rotationVector.yCoord * 4.0, rotationVector.zCoord * 4.0);
                  MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                  if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                     VecRotation currentVec = new VecRotation(posVec, rotation);
                     if (vecRotation == null || getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())) {
                        vecRotation = currentVec;
                     }
                  }
               }
            }
         }

         if (vecRotation != null) {
            setTargetRotation(vecRotation.getRotation());
         }

         return vecRotation;
      }
   }

   public static void faceBow(Entity target, boolean silent, boolean predict, float predictSize) {
      EntityPlayerSP player = mc.thePlayer;
      double posX = target.posX + (predict ? (target.posX - target.prevPosX) * (double)predictSize : 0.0) - (player.posX + (predict ? player.posX - player.prevPosX : 0.0));
      double posY = target.getEntityBoundingBox().minY + (predict ? (target.getEntityBoundingBox().minY - target.prevPosY) * (double)predictSize : 0.0) + (double)target.getEyeHeight() - 0.15 - (player.getEntityBoundingBox().minY + (predict ? player.posY - player.prevPosY : 0.0)) - (double)player.getEyeHeight();
      double posZ = target.posZ + (predict ? (target.posZ - target.prevPosZ) * (double)predictSize : 0.0) - (player.posZ + (predict ? player.posZ - player.prevPosZ : 0.0));
      double posSqrt = Math.sqrt(posX * posX + posZ * posZ);
      float velocity = (float)player.getItemInUseDuration() / 20.0F;
      velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;
      if (velocity > 1.0F) {
         velocity = 1.0F;
      }

      Rotation rotation = new Rotation((float)(Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0F, (float)(-Math.toDegrees(Math.atan(((double)(velocity * velocity) - Math.sqrt((double)(velocity * velocity * velocity * velocity) - 0.006000000052154064 * (0.006000000052154064 * posSqrt * posSqrt + 2.0 * posY * (double)(velocity * velocity)))) / (0.006000000052154064 * posSqrt)))));
      if (silent) {
         setTargetRotation(rotation);
      } else {
         limitAngleChange(new Rotation(player.rotationYaw, player.rotationPitch), rotation, (float)(10 + (new Random()).nextInt(6))).toPlayer(mc.thePlayer);
      }

   }

   public static Rotation toRotation(Vec3 vec, boolean predict) {
      Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      if (predict) {
         eyesPos.addVector(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
      }

      double diffX = vec.xCoord - eyesPos.xCoord;
      double diffY = vec.yCoord - eyesPos.yCoord;
      double diffZ = vec.zCoord - eyesPos.zCoord;
      return new Rotation(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
   }

   public static Vec3 getCenter(AxisAlignedBB bb) {
      return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
   }

   public static VecRotation searchCenter(AxisAlignedBB bb, boolean outborder, boolean random, boolean predict, boolean throughWalls) {
      Vec3 randomVec;
      if (outborder) {
         randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * (x * 0.3 + 1.0), bb.minY + (bb.maxY - bb.minY) * (y * 0.3 + 1.0), bb.minZ + (bb.maxZ - bb.minZ) * (z * 0.3 + 1.0));
         return new VecRotation(randomVec, toRotation(randomVec, predict));
      } else {
         randomVec = new Vec3(bb.minX + (bb.maxX - bb.minX) * x * 0.8, bb.minY + (bb.maxY - bb.minY) * y * 0.8, bb.minZ + (bb.maxZ - bb.minZ) * z * 0.8);
         Rotation randomRotation = toRotation(randomVec, predict);
         VecRotation vecRotation = null;

         for(double xSearch = 0.15; xSearch < 0.85; xSearch += 0.1) {
            for(double ySearch = 0.15; ySearch < 1.0; ySearch += 0.1) {
               for(double zSearch = 0.15; zSearch < 0.85; zSearch += 0.1) {
                  Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch, bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                  Rotation rotation = toRotation(vec3, predict);
                  if (throughWalls || isVisible(vec3)) {
                     VecRotation currentVec = new VecRotation(vec3, rotation);
                     if (vecRotation != null) {
                        if (random) {
                           if (!(getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation))) {
                              continue;
                           }
                        } else if (!(getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation()))) {
                           continue;
                        }
                     }

                     vecRotation = currentVec;
                  }
               }
            }
         }

         return vecRotation;
      }
   }

   public static double getRotationDifference(Entity entity) {
      Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);
      return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
   }

   public static double getRotationDifference(Rotation rotation) {
      return serverRotation == null ? 0.0 : getRotationDifference(rotation, serverRotation);
   }

   public static double getRotationDifference(Rotation a, Rotation b) {
      return Math.hypot((double)getAngleDifference(a.getYaw(), b.getYaw()), (double)(a.getPitch() - b.getPitch()));
   }

   public static Rotation limitAngleChange(Rotation currentRotation, Rotation targetRotation, float turnSpeed) {
      float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
      float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());
      return new Rotation(currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)), currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
   }

   private static float getAngleDifference(float a, float b) {
      return ((a - b) % 360.0F + 540.0F) % 360.0F - 180.0F;
   }

   public static Vec3 getVectorForRotation(Rotation rotation) {
      float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - 3.1415927F);
      float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - 3.1415927F);
      float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
      float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
      return new Vec3((double)(yawSin * pitchCos), (double)pitchSin, (double)(yawCos * pitchCos));
   }

   public static boolean isVisible(Vec3 vec3) {
      Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
      return mc.theWorld.rayTraceBlocks(eyesPos, vec3) == null;
   }

   public static void onTick(TickEvent event) {
      if (targetRotation != null) {
         --keepLength;
         if (keepLength <= 0) {
            reset();
         }
      }

      if (random.nextGaussian() > 0.8) {
         x = Math.random();
      }

      if (random.nextGaussian() > 0.8) {
         y = Math.random();
      }

      if (random.nextGaussian() > 0.8) {
         z = Math.random();
      }

   }

   public static void onPacket(PacketEvent event) {
      if (event.isOutGoing()) {
         Packet packet = event.getPacket();
         if (packet instanceof C03PacketPlayer) {
            C03PacketPlayer packetPlayer = (C03PacketPlayer)packet;
            if (targetRotation != null && !keepCurrentRotation && (targetRotation.getYaw() != serverRotation.getYaw() || targetRotation.getPitch() != serverRotation.getPitch())) {
               packetPlayer.yaw = targetRotation.getYaw();
               packetPlayer.pitch = targetRotation.getPitch();
               packetPlayer.rotating = true;
            }

            if (packetPlayer.rotating) {
               serverRotation = new Rotation(packetPlayer.yaw, packetPlayer.pitch);
            }
         }

      }
   }

   public static void onUpdateA(UpdateEvent event) {
      if (event.isPre()) {
         boolean rotating = false;
         if (targetRotation != null && !keepCurrentRotation) {
            if (targetRotation.getYaw() == serverRotation.getYaw() && targetRotation.getPitch() == serverRotation.getPitch()) {
               event.setYaw(targetRotation.getYaw() + 1.0F);
               event.setPitch(targetRotation.getPitch());
            } else {
               event.setYaw(targetRotation.getYaw());
               event.setPitch(targetRotation.getPitch());
            }

            rotating = true;
         }

         if (rotating) {
            serverRotation = new Rotation(event.getYaw(), event.getPitch());
         }

      }
   }

   public static void setTargetRotation(Rotation rotation) {
      setTargetRotation(rotation, 0);
   }

   public static void setTargetRotation(Rotation rotation, int keepLength) {
      if (!Double.isNaN((double)rotation.getYaw()) && !Double.isNaN((double)rotation.getPitch()) && !(rotation.getPitch() > 90.0F) && !(rotation.getPitch() < -90.0F)) {
         rotation.fixedSensitivity(mc.gameSettings.mouseSensitivity);
         targetRotation = rotation;
         RotationUtil.keepLength = keepLength;
      }
   }

   public static void reset() {
      keepLength = 0;
      targetRotation = null;
   }
}
