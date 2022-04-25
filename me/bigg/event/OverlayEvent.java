package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class OverlayEvent implements Event {
   private final float partialTicks;
   private final ScaledResolution scaledResolution;

   public OverlayEvent(float ticks, ScaledResolution scaled) {
      this.partialTicks = ticks;
      this.scaledResolution = scaled;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }
}
