package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.Entity;

public class AttackEvent implements Event {
   private final Entity ent;
   private final boolean pre;

   public AttackEvent(Entity ent, boolean pre) {
      this.ent = ent;
      this.pre = pre;
   }

   public Entity getEntity() {
      return this.ent;
   }

   public boolean isPre() {
      return this.pre;
   }

   public boolean isPost() {
      return !this.pre;
   }
}
