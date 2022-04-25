package me.bigg.module.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import me.bigg.event.RenderEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.RenderUtil;
import me.bigg.util.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class BedBreaker extends Module {
   private BlockPos blockBreaking;
   private final TimerUtil timer = new TimerUtil();
   private final ArrayList bedList = new ArrayList();

   public BedBreaker() {
      super("Bed Breaker", "fucker", Category.Misc);
   }

   public void onEnable() {
      super.onEnable();
      this.blockBreaking = null;
      this.bedList.clear();
      this.timer.reset();
   }

   @EventTarget
   void onUpdate(UpdateEvent e) {
      if (e.isPre()) {
         int reach = 6;

         int i;
         for(int y = reach; y >= -reach; --y) {
            for(i = -reach; i <= reach; ++i) {
               for(int z = -reach; z <= reach; ++z) {
                  BlockPos pos = new BlockPos(mc.thePlayer.posX + (double)i, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z);
                  if (this.blockChecks(mc.theWorld.getBlockState(pos).getBlock()) && mc.thePlayer.getDistance(mc.thePlayer.posX + (double)i, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z) < (double)mc.playerController.getBlockReachDistance() - 0.2 && !this.bedList.contains(pos)) {
                     this.bedList.add(pos);
                  }
               }
            }
         }

         BlockPos closest = null;
         if (!this.bedList.isEmpty()) {
            for(i = 0; i < this.bedList.size(); ++i) {
               BlockPos bed = (BlockPos)this.bedList.get(i);
               if (mc.thePlayer.getDistance((double)bed.getX(), (double)bed.getY(), (double)bed.getZ()) > (double)mc.playerController.getBlockReachDistance() - 0.2 || mc.theWorld.getBlockState(bed).getBlock() != Blocks.bed) {
                  this.bedList.remove(i);
               }

               if (closest == null) {
                  closest = bed;
               }
            }
         }

         if (closest != null) {
            float[] rot = this.getRotations(closest);
            e.setYaw(rot[0]);
            e.setPitch(rot[1]);
            this.blockBreaking = closest;
            return;
         }

         this.blockBreaking = null;
      }

      if (e.isPost() && this.blockBreaking != null) {
         if (mc.playerController.blockHitDelay > 1) {
            mc.playerController.blockHitDelay = 1;
         }

         mc.thePlayer.swingItem();
         EnumFacing direction = this.getClosestEnum(this.blockBreaking);
         if (direction != null) {
            mc.playerController.onPlayerDamageBlock(this.blockBreaking, direction);
         }
      }

   }

   @EventTarget
   void onRender(RenderEvent event) {
      if (this.blockBreaking != null) {
         GlStateManager.pushMatrix();
         GlStateManager.disableDepth();
         RenderUtil.drawBoundingBox((double)this.blockBreaking.getX() - mc.getRenderManager().renderPosX + 0.5, (double)this.blockBreaking.getY() - mc.getRenderManager().renderPosY, (double)this.blockBreaking.getZ() - mc.getRenderManager().renderPosZ + 0.5, 0.5, 0.5625, 1.0F, 0.0F, 0.0F, 0.25F);
         GlStateManager.enableDepth();
         GlStateManager.popMatrix();
      }
   }

   private boolean blockChecks(Block block) {
      return block == Blocks.bed;
   }

   public float[] getRotations(BlockPos block) {
      double x = (double)block.getX() + 0.5 - mc.thePlayer.posX;
      double z = (double)block.getZ() + 0.5 - mc.thePlayer.posZ;
      double d1 = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - ((double)block.getY() + 0.5);
      double d3 = (double)MathHelper.sqrt_double(x * x + z * z);
      float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
      float pitch = (float)(Math.atan2(d1, d3) * 180.0 / Math.PI);
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      return new float[]{yaw, pitch};
   }

   private EnumFacing getClosestEnum(BlockPos pos) {
      EnumFacing closestEnum = EnumFacing.UP;
      float rotations = MathHelper.wrapAngleTo180_float(this.getRotations(pos)[0]);
      if (rotations >= 45.0F && rotations <= 135.0F) {
         closestEnum = EnumFacing.EAST;
      } else if ((!(rotations >= 135.0F) || !(rotations <= 180.0F)) && (!(rotations <= -135.0F) || !(rotations >= -180.0F))) {
         if (rotations <= -45.0F && rotations >= -135.0F) {
            closestEnum = EnumFacing.WEST;
         } else if (rotations >= -45.0F && rotations <= 0.0F || rotations <= 45.0F && rotations >= 0.0F) {
            closestEnum = EnumFacing.NORTH;
         }
      } else {
         closestEnum = EnumFacing.SOUTH;
      }

      if (MathHelper.wrapAngleTo180_float(this.getRotations(pos)[1]) > 75.0F || MathHelper.wrapAngleTo180_float(this.getRotations(pos)[1]) < -75.0F) {
         closestEnum = EnumFacing.UP;
      }

      return closestEnum;
   }

   private EnumFacing getFacingDirection(BlockPos pos) {
      EnumFacing direction = null;
      if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isFullCube() && !(mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockBed)) {
         direction = EnumFacing.UP;
      } else if (!mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock().isFullCube() && !(mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof BlockBed)) {
         direction = EnumFacing.DOWN;
      } else if (!mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock().isFullCube() && !(mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() instanceof BlockBed)) {
         direction = EnumFacing.EAST;
      } else if (!mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock().isFullCube() && !(mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() instanceof BlockBed)) {
         direction = EnumFacing.WEST;
      } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isFullCube() && !(mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() instanceof BlockBed)) {
         direction = EnumFacing.SOUTH;
      } else if (!mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock().isFullCube() && !(mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() instanceof BlockBed)) {
         direction = EnumFacing.NORTH;
      }

      MovingObjectPosition rayResult = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5));
      return rayResult != null && rayResult.getBlockPos() == pos ? rayResult.sideHit : direction;
   }
}
