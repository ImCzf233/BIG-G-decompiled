package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;

public class MoveEvent implements Event {
   private double x;
   private double y;
   private double z;

   public MoveEvent(double targetX, double targetY, double targetZ) {
      this.x = targetX;
      this.y = targetY;
      this.z = targetZ;
   }

   public double getX() {
      return this.x;
   }

   public void setX(double targetX) {
      this.x = targetX;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double targetY) {
      this.y = targetY;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double targetZ) {
      this.z = targetZ;
   }
}
