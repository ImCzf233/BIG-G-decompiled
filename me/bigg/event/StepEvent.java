package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;

public class StepEvent implements Event {
   private double stepHeight;
   private boolean pre;

   public StepEvent(double stepHeight, boolean pre) {
      this.stepHeight = stepHeight;
      this.pre = pre;
   }

   public double getStepHeight() {
      return this.stepHeight;
   }

   public void setStepHeight(double stepHeight) {
      this.stepHeight = stepHeight;
   }

   public boolean isPre() {
      return this.pre;
   }

   public boolean isPost() {
      return !this.pre;
   }
}
