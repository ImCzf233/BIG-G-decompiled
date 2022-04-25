package me.bigg.util;

import me.bigg.event.MoveEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtil {
   private static final Minecraft mc = Minecraft.getMinecraft();

   public static double getBaseMoveSpeed() {
      double baseSpeed = 0.2873;
      if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
         int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
      }

      return baseSpeed;
   }

   public static boolean isBlockUnder() {
      if (mc.thePlayer.posY < 0.0) {
         return false;
      } else {
         for(int off = 0; off < (int)mc.thePlayer.posY + 2; off += 2) {
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, (double)(-off), 0.0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isBlockUnder(Entity ent) {
      if (ent.posY < 0.0) {
         return false;
      } else {
         for(int off = 0; off < (int)ent.posY + 2; off += 2) {
            AxisAlignedBB bb = ent.getEntityBoundingBox().offset(0.0, (double)(-off), 0.0);
            if (!mc.theWorld.getCollidingBoundingBoxes(ent, bb).isEmpty()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isAirUnder(Entity ent) {
      return mc.theWorld.getBlockState(new BlockPos(ent.posX, ent.posY - 1.0, ent.posZ)).getBlock() == Blocks.air;
   }

   public static void TeleportByDist(double dist) {
      double forward = (double)mc.thePlayer.movementInput.moveForward;
      double strafe = (double)mc.thePlayer.movementInput.moveStrafe;
      float yaw = mc.thePlayer.rotationYaw;
      if (forward != 0.0) {
         if (strafe > 0.0) {
            yaw += (float)(forward > 0.0 ? -45 : 45);
         } else if (strafe < 0.0) {
            yaw += (float)(forward > 0.0 ? 45 : -45);
         }

         strafe = 0.0;
         if (forward > 0.0) {
            forward = 1.0;
         } else if (forward < 0.0) {
            forward = -1.0;
         }
      }

      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      double x = forward * dist * cos + strafe * dist * sin;
      double z = forward * dist * sin - strafe * dist * cos;
      mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
   }

   public static void doStrafe() {
      if (MovementInput()) {
         float rotationYaw = mc.thePlayer.rotationYaw;
         if (mc.thePlayer.moveForward < 0.0F) {
            rotationYaw += 180.0F;
         }

         float forward = 1.0F;
         if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
         } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
         }

         if (mc.thePlayer.moveStrafing > 0.0F) {
            rotationYaw -= 90.0F * forward;
         }

         if (mc.thePlayer.moveStrafing < 0.0F) {
            rotationYaw += 90.0F * forward;
         }

         double direction = Math.toRadians((double)rotationYaw);
         float speed = (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
         mc.thePlayer.motionX = -Math.sin(direction) * (double)speed;
         mc.thePlayer.motionZ = Math.cos(direction) * (double)speed;
      }
   }

   public static void doStrafe(double speedA) {
      if (MovementInput()) {
         float rotationYaw = mc.thePlayer.rotationYaw;
         if (mc.thePlayer.moveForward < 0.0F) {
            rotationYaw += 180.0F;
         }

         float forward = 1.0F;
         if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
         } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
         }

         if (mc.thePlayer.moveStrafing > 0.0F) {
            rotationYaw -= 90.0F * forward;
         }

         if (mc.thePlayer.moveStrafing < 0.0F) {
            rotationYaw += 90.0F * forward;
         }

         double direction = Math.toRadians((double)rotationYaw);
         float speed = (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
         mc.thePlayer.motionX = -Math.sin(direction) * speedA;
         mc.thePlayer.motionZ = Math.cos(direction) * speedA;
      }
   }

   public static boolean MovementInput() {
      return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown();
   }

   public static boolean isOnGround(double height) {
      return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
   }

   public static boolean isOnGround(double height, Entity e) {
      return !mc.theWorld.getCollidingBoundingBoxes(e, e.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
   }

   public static int getJumpEffect() {
      return mc.thePlayer.isPotionActive(Potion.jump) ? mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0;
   }

   public static int getSpeedEffect() {
      return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
   }

   public static boolean isHoldingFood(Item item) {
      return !(item instanceof ItemSword) && !(item instanceof ItemBow);
   }

   public static boolean InsideBlock() {
      for(int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
         for(int y = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxY) + 1; ++y) {
            for(int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
               Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
               if (block != null && !(block instanceof BlockAir)) {
                  AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                  if (block instanceof BlockHopper) {
                     boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
                  }

                  if (boundingBox != null && mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static boolean InsideBlock(EntityPlayer player) {
      for(int x = MathHelper.floor_double(player.getEntityBoundingBox().minX); x < MathHelper.floor_double(player.getEntityBoundingBox().maxX) + 1; ++x) {
         for(int y = MathHelper.floor_double(player.getEntityBoundingBox().minY); y < MathHelper.floor_double(player.getEntityBoundingBox().maxY) + 1; ++y) {
            for(int z = MathHelper.floor_double(player.getEntityBoundingBox().minZ); z < MathHelper.floor_double(player.getEntityBoundingBox().maxZ) + 1; ++z) {
               Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
               if (block != null && !(block instanceof BlockAir)) {
                  AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                  if (block instanceof BlockHopper) {
                     boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
                  }

                  if (boundingBox != null && player.getEntityBoundingBox().intersectsWith(boundingBox)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static boolean isInLiquid() {
      if (mc.thePlayer.isInWater()) {
         return true;
      } else {
         boolean inLiquid = false;
         int y = (int)mc.thePlayer.getEntityBoundingBox().minY;

         for(int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for(int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
               Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
               if (block != null && block.getMaterial() != Material.air) {
                  if (!(block instanceof BlockLiquid)) {
                     return false;
                  }

                  inLiquid = true;
               }
            }
         }

         return inLiquid;
      }
   }

   public static boolean isOnLiquid() {
      AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox();
      if (boundingBox == null) {
         return false;
      } else {
         boundingBox = boundingBox.contract(0.01, 0.0, 0.01).offset(0.0, -0.01, 0.0);
         boolean onLiquid = false;
         int y = (int)boundingBox.minY;

         for(int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper.floor_double(boundingBox.maxX + 1.0); ++x) {
            for(int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper.floor_double(boundingBox.maxZ + 1.0); ++z) {
               Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
               if (block != Blocks.air) {
                  if (!(block instanceof BlockLiquid)) {
                     return false;
                  }

                  onLiquid = true;
               }
            }
         }

         return onLiquid;
      }
   }

   public static boolean isOnLiquid(double profondeur) {
      boolean onLiquid = false;
      if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - profondeur, mc.thePlayer.posZ)).getBlock().getMaterial().isLiquid()) {
         onLiquid = true;
      }

      return onLiquid;
   }

   public static boolean isTotalOnLiquid(double profondeur) {
      for(double x = mc.thePlayer.getEntityBoundingBox().minX; x < mc.thePlayer.getEntityBoundingBox().maxX; x += 0.009999999776482582) {
         for(double z = mc.thePlayer.getEntityBoundingBox().minZ; z < mc.thePlayer.getEntityBoundingBox().maxZ; z += 0.009999999776482582) {
            Block block = mc.theWorld.getBlockState(new BlockPos(x, mc.thePlayer.posY - profondeur, z)).getBlock();
            if (!(block instanceof BlockLiquid) && !(block instanceof BlockAir)) {
               return false;
            }
         }
      }

      return true;
   }

   public static void setSpeed(MoveEvent event, double speed) {
      float yaw = mc.thePlayer.rotationYaw;
      double forward = (double)mc.thePlayer.movementInput.moveForward;
      double strafe = (double)mc.thePlayer.movementInput.moveStrafe;
      if (forward == 0.0 && strafe == 0.0) {
         event.setX(0.0);
         event.setZ(0.0);
      } else {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += (float)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
               yaw += (float)(forward > 0.0 ? 45 : -45);
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else {
               forward = -1.0;
            }
         }

         event.setX(forward * speed * Math.cos(Math.toRadians((double)yaw + 87.88867)) + strafe * speed * Math.sin(Math.toRadians((double)yaw + 87.88867)));
         event.setZ(forward * speed * Math.sin(Math.toRadians((double)yaw + 87.88867)) - strafe * speed * Math.cos(Math.toRadians((double)yaw + 87.88867)));
      }

   }

   public static void setSpeed(double speed) {
      float yaw = mc.thePlayer.rotationYaw;
      double forward = (double)mc.thePlayer.movementInput.moveForward;
      double strafe = (double)mc.thePlayer.movementInput.moveStrafe;
      if (forward == 0.0 && strafe == 0.0) {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
      } else {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += (float)(forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
               yaw += (float)(forward > 0.0 ? 45 : -45);
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else {
               forward = -1.0;
            }
         }

         mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F)));
         mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      }

   }

   public static double getLastDist() {
      double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      return Math.sqrt(xDist * xDist + zDist * zDist);
   }

   public static double getLastDist(Entity entIn) {
      double xDist = entIn.posX - entIn.prevPosX;
      double zDist = entIn.posZ - entIn.prevPosZ;
      return Math.sqrt(xDist * xDist + zDist * zDist);
   }

   public static double getBPS() {
      double bps = getLastDist() * 20.0;
      return ((double)((int)bps) + bps - (double)((int)bps)) * (double)mc.timer.timerSpeed;
   }

   public static double getBPS(Entity entityIn) {
      double bps = getLastDist(entityIn) * 20.0;
      return (double)((int)bps) + bps - (double)((int)bps);
   }
}
