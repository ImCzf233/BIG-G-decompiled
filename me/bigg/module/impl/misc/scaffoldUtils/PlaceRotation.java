package me.bigg.module.impl.misc.scaffoldUtils;

public class PlaceRotation {
   private PlaceInfo info;
   private Rotation rot;

   public PlaceRotation(PlaceInfo info, Rotation rot) {
      this.info = info;
      this.rot = rot;
   }

   public PlaceInfo getPlaceInfo() {
      return this.info;
   }

   public Rotation getRotation() {
      return this.rot;
   }
}
