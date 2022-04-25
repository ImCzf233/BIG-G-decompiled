package me.bigg.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.entity.Entity;

public class RenderNameEvent extends EventCancellable {
   private final Entity entity;

   public RenderNameEvent(Entity entityIn) {
      this.entity = entityIn;
   }

   public Entity getEntity() {
      return this.entity;
   }
}
