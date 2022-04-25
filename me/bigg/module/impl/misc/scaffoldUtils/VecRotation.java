package me.bigg.module.impl.misc.scaffoldUtils;

import net.minecraft.util.Vec3;

public final class VecRotation {
   private Vec3 vec;
   private Rotation rot;

   public VecRotation() {
   }

   public VecRotation(Vec3 vec, Rotation rot) {
      this.vec = vec;
      this.rot = rot;
   }

   public Rotation getRotation() {
      return this.rot;
   }

   public Vec3 getVec() {
      return this.vec;
   }
}
