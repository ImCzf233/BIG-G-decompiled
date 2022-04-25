package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;

public class RenderEvent implements Event {
   private final float partialTicks;

   public RenderEvent(float ticks) {
      this.partialTicks = ticks;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
