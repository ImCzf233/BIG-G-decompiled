package me.bigg.module.impl.misc.scaffoldUtils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class PlaceInfo {
   private BlockPos pos;
   private EnumFacing enumFacing;
   private Vec3 vec3;

   public PlaceInfo(BlockPos pos, EnumFacing enumFacing) {
      this.pos = pos;
      this.enumFacing = enumFacing;
      this.vec3 = new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
   }

   public PlaceInfo(BlockPos pos, EnumFacing enumFacing, Vec3 vec3) {
      this.pos = pos;
      this.enumFacing = enumFacing;
      this.vec3 = vec3;
   }

   public Vec3 getVec3() {
      return this.vec3;
   }

   public BlockPos getBlockPos() {
      return this.pos;
   }

   public EnumFacing getEnumFacing() {
      return this.enumFacing;
   }

   public PlaceInfo get(BlockPos blockpos) {
      if (BlockUtils.canBeClicked(blockpos.add(0, -1, 0))) {
         return new PlaceInfo(blockpos.add(0, -1, 0), EnumFacing.UP);
      } else if (BlockUtils.canBeClicked(blockpos.add(0, 0, 1))) {
         return new PlaceInfo(blockpos.add(0, 0, 1), EnumFacing.NORTH);
      } else if (BlockUtils.canBeClicked(blockpos.add(0, 0, -1))) {
         return new PlaceInfo(blockpos.add(0, 0, -1), EnumFacing.SOUTH);
      } else if (BlockUtils.canBeClicked(blockpos.add(-1, 0, 0))) {
         return new PlaceInfo(blockpos.add(-1, 0, 0), EnumFacing.EAST);
      } else {
         return BlockUtils.canBeClicked(blockpos.add(1, 0, 0)) ? new PlaceInfo(blockpos.add(1, 0, 0), EnumFacing.WEST) : null;
      }
   }
}
