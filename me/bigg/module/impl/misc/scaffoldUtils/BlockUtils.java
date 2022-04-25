package me.bigg.module.impl.misc.scaffoldUtils;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class BlockUtils {
   static double x;
   static double y;
   static double z;
   static double xPreEn;
   static double yPreEn;
   static double zPreEn;
   static double xPre;
   static double yPre;
   static double zPre;

   public static float[] getFacingRotations(int x, int y, int z, EnumFacing facing) {
      EntitySnowball temp = new EntitySnowball(Minecraft.getMinecraft().theWorld);
      temp.posX = (double)x + 0.5;
      temp.posY = (double)y + 0.5;
      temp.posZ = (double)z + 0.5;
      temp.posX += (double)facing.getDirectionVec().getX() * 0.25;
      temp.posY += (double)facing.getDirectionVec().getY() * 0.25;
      temp.posZ += (double)facing.getDirectionVec().getZ() * 0.25;
      return null;
   }

   public static boolean isOnLiquid() {
      boolean onLiquid = false;
      if (getBlockAtPosC(Minecraft.getMinecraft().thePlayer, 0.30000001192092896, 0.10000000149011612, 0.30000001192092896).getMaterial().isLiquid() && getBlockAtPosC(Minecraft.getMinecraft().thePlayer, -0.30000001192092896, 0.10000000149011612, -0.30000001192092896).getMaterial().isLiquid()) {
         onLiquid = true;
      }

      return onLiquid;
   }

   public static boolean isOnLadder() {
      if (Minecraft.getMinecraft().thePlayer == null) {
         return false;
      } else {
         boolean onLadder = false;
         int y = (int)Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0, 1.0, 0.0).minY;

         for(int x = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for(int z = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
               Block block = getBlock(x, y, z);
               if (block != null && !(block instanceof BlockAir)) {
                  if (!(block instanceof BlockLadder) && !(block instanceof BlockVine)) {
                     return false;
                  }

                  onLadder = true;
               }
            }
         }

         if (!onLadder && !Minecraft.getMinecraft().thePlayer.isOnLadder()) {
            return false;
         } else {
            return true;
         }
      }
   }

   public static boolean isOnIce() {
      if (Minecraft.getMinecraft().thePlayer == null) {
         return false;
      } else {
         boolean onIce = false;
         int y = (int)Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0, -0.01, 0.0).minY;

         for(int x = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for(int z = MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
               Block block = getBlock(x, y, z);
               if (block != null && !(block instanceof BlockAir)) {
                  if (!(block instanceof BlockIce) && !(block instanceof BlockPackedIce)) {
                     return false;
                  }

                  onIce = true;
               }
            }
         }

         return onIce;
      }
   }

   public Block getBlockByIDorName(String message) {
      Block tBlock = null;

      try {
         tBlock = Block.getBlockById(Integer.parseInt(message));
      } catch (NumberFormatException var8) {
         Block block = null;
         Iterator var6 = Block.blockRegistry.iterator();

         while(var6.hasNext()) {
            Object object = var6.next();
            block = (Block)object;
            String label = block.getLocalizedName().replace(" ", "");
            if (label.toLowerCase().startsWith(message) || label.toLowerCase().contains(message)) {
               break;
            }
         }

         if (block != null) {
            tBlock = block;
         }
      }

      return tBlock;
   }

   public static boolean isBlockUnderPlayer(Material material, float height) {
      return getBlockAtPosC(Minecraft.getMinecraft().thePlayer, 0.3100000023841858, (double)height, 0.3100000023841858).getMaterial() == material && getBlockAtPosC(Minecraft.getMinecraft().thePlayer, -0.3100000023841858, (double)height, -0.3100000023841858).getMaterial() == material && getBlockAtPosC(Minecraft.getMinecraft().thePlayer, -0.3100000023841858, (double)height, 0.3100000023841858).getMaterial() == material && getBlockAtPosC(Minecraft.getMinecraft().thePlayer, 0.3100000023841858, (double)height, -0.3100000023841858).getMaterial() == material;
   }

   public static Block getBlockAtPosC(EntityPlayer inPlayer, double x, double y, double z) {
      return getBlock(new BlockPos(inPlayer.posX - x, inPlayer.posY - y, inPlayer.posZ - z));
   }

   public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
      return getBlock(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ));
   }

   public static Block getBlockAbovePlayer(EntityPlayer inPlayer, double height) {
      return getBlock(new BlockPos(inPlayer.posX, inPlayer.posY + (double)inPlayer.height + height, inPlayer.posZ));
   }

   public static Block getBlock(int x, int y, int z) {
      return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
   }

   public static Block getBlock(BlockPos pos) {
      return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
   }

   private static void preInfiniteReach(double range, double maxXZTP, double maxYTP, ArrayList positionsBack, ArrayList positions, Vec3 targetPos, boolean tpStraight, boolean up, boolean attack, boolean tpUpOneBlock, boolean sneaking) {
   }

   private static void postInfiniteReach() {
   }

   public static Block getBlock(double x, double y, double z) {
      return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock();
   }

   public static double normalizeAngle(double angle) {
      return (angle + 360.0) % 360.0;
   }

   public static float normalizeAngle(float angle) {
      return (angle + 360.0F) % 360.0F;
   }

   public static float[] getFacePos(Vec3 vec) {
      double n = vec.xCoord + 0.5;
      Minecraft.getMinecraft();
      double diffX = n - Minecraft.getMinecraft().thePlayer.posX;
      double n2 = vec.yCoord + 0.5;
      Minecraft.getMinecraft();
      double posY = Minecraft.getMinecraft().thePlayer.posY;
      Minecraft.getMinecraft();
      double diffY = n2 - (posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
      double n3 = vec.zCoord + 0.5;
      Minecraft.getMinecraft();
      double diffZ = n3 - Minecraft.getMinecraft().thePlayer.posZ;
      double dist = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
      float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
      float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
      float[] array = new float[2];
      int n4 = false;
      Minecraft.getMinecraft();
      float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
      Minecraft.getMinecraft();
      array[0] = rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw);
      int n6 = true;
      Minecraft.getMinecraft();
      float rotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
      Minecraft.getMinecraft();
      array[1] = rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch);
      return array;
   }

   public static float[] getFacePosRemote(Vec3 src, Vec3 dest) {
      double diffX = dest.xCoord - src.xCoord;
      double diffY = dest.yCoord - src.yCoord;
      double diffZ = dest.zCoord - src.zCoord;
      double dist = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
      float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
      float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
      return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
   }

   public static MovingObjectPosition rayTracePos(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
      float[] rots = getFacePosRemote(vec32, vec31);
      float yaw = rots[0];
      double angleA = Math.toRadians((double)normalizeAngle(yaw));
      double angleB = Math.toRadians((double)(normalizeAngle(yaw) + 180.0F));
      double size = 2.1;
      double size2 = 2.1;
      Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
      Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
      Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
      Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
      new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
      new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
      new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
      new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
      MovingObjectPosition trace1 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(left, left2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
      MovingObjectPosition trace2 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
      MovingObjectPosition trace3 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(right, right2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
      MovingObjectPosition trace4 = null;
      MovingObjectPosition trace5 = null;
      if (trace2 != null || trace1 != null || trace3 != null || trace4 != null || trace5 != null) {
         if (returnLastUncollidableBlock) {
            if (trace5 != null && (getBlock(((MovingObjectPosition)trace5).getBlockPos()).getMaterial() != Material.air || ((MovingObjectPosition)trace5).entityHit != null)) {
               return (MovingObjectPosition)trace5;
            }

            if (trace4 != null && (getBlock(((MovingObjectPosition)trace4).getBlockPos()).getMaterial() != Material.air || ((MovingObjectPosition)trace4).entityHit != null)) {
               return (MovingObjectPosition)trace4;
            }

            if (trace3 != null && (getBlock(trace3.getBlockPos()).getMaterial() != Material.air || trace3.entityHit != null)) {
               return trace3;
            }

            if (trace1 != null && (getBlock(trace1.getBlockPos()).getMaterial() != Material.air || trace1.entityHit != null)) {
               return trace1;
            }

            if (trace2 != null && (getBlock(trace2.getBlockPos()).getMaterial() != Material.air || trace2.entityHit != null)) {
               return trace2;
            }
         } else {
            if (trace5 != null) {
               return (MovingObjectPosition)trace5;
            }

            if (trace4 != null) {
               return (MovingObjectPosition)trace4;
            }

            if (trace3 != null) {
               return trace3;
            }

            if (trace1 != null) {
               return trace1;
            }

            if (trace2 != null) {
               return trace2;
            }
         }
      }

      if (trace2 != null) {
         return trace2;
      } else if (trace3 != null) {
         return trace3;
      } else if (trace1 != null) {
         return trace1;
      } else if (trace5 != null) {
         return (MovingObjectPosition)trace5;
      } else if (trace4 == null) {
         return null;
      } else {
         return (MovingObjectPosition)trace4;
      }
   }

   public static boolean rayTraceWide(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
      float yaw = getFacePosRemote(vec32, vec31)[0];
      yaw = normalizeAngle(yaw);
      yaw += 180.0F;
      yaw = MathHelper.wrapAngleTo180_float(yaw);
      double angleA = Math.toRadians((double)yaw);
      double angleB = Math.toRadians((double)(yaw + 180.0F));
      double size = 2.1;
      double size2 = 2.1;
      Vec3 left = new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
      Vec3 right = new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
      Vec3 left2 = new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
      Vec3 right2 = new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
      new Vec3(vec31.xCoord + Math.cos(angleA) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleA) * 2.1);
      new Vec3(vec31.xCoord + Math.cos(angleB) * 2.1, vec31.yCoord, vec31.zCoord + Math.sin(angleB) * 2.1);
      new Vec3(vec32.xCoord + Math.cos(angleA) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleA) * 2.1);
      new Vec3(vec32.xCoord + Math.cos(angleB) * 2.1, vec32.yCoord, vec32.zCoord + Math.sin(angleB) * 2.1);
      MovingObjectPosition trace1 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(left, left2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
      MovingObjectPosition trace2 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
      MovingObjectPosition trace3 = Minecraft.getMinecraft().theWorld.rayTraceBlocks(right, right2, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
      MovingObjectPosition trace4 = null;
      MovingObjectPosition trace5 = null;
      if (!returnLastUncollidableBlock) {
         return trace1 != null || trace2 != null || trace3 != null || trace5 != null || trace4 != null;
      } else {
         return trace1 != null && getBlock(trace1.getBlockPos()).getMaterial() != Material.air || trace2 != null && getBlock(trace2.getBlockPos()).getMaterial() != Material.air || trace3 != null && getBlock(trace3.getBlockPos()).getMaterial() != Material.air || trace4 != null && getBlock(((MovingObjectPosition)trace4).getBlockPos()).getMaterial() != Material.air || trace5 != null && getBlock(((MovingObjectPosition)trace5).getBlockPos()).getMaterial() != Material.air;
      }
   }

   public static boolean canBeClicked(BlockPos pos) {
      return getBlock(pos).canCollideCheck(getState(pos), false);
   }

   public static IBlockState getState(BlockPos pos) {
      return Minecraft.getMinecraft().theWorld.getBlockState(pos);
   }

   private static PlayerControllerMP getPlayerController() {
      Minecraft.getMinecraft();
      return Minecraft.getMinecraft().playerController;
   }

   public static void processRightClickBlock(EntityPlayerSP thePlayer, WorldClient theWorld, ItemStack itemStack, BlockPos pos, EnumFacing side, Vec3 hitVec) {
   }

   public static final Material getMaterial(BlockPos blockPos) {
      Block var10000 = getBlock(blockPos);
      return var10000 != null ? var10000.getMaterial() : null;
   }

   public static final boolean isReplaceable(BlockPos blockPos) {
      Material var10000 = getMaterial(blockPos);
      return var10000 != null ? var10000.isReplaceable() : false;
   }
}
