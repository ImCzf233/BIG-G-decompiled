package me.bigg.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.entity.Entity;

public class RenderArmEvent extends EventCancellable {
   private Entity entity;
   private boolean pre;

   public RenderArmEvent(Entity entity, boolean pre) {
      this.entity = entity;
      this.pre = pre;
   }

   public boolean isPre() {
      return this.pre;
   }

   public boolean isPost() {
      return !this.pre;
   }

   public Entity getEntity() {
      return this.entity;
   }
}
