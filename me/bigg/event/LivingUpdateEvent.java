package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.EntityLivingBase;

public class LivingUpdateEvent implements Event {
   private final EntityLivingBase entity;

   public LivingUpdateEvent(EntityLivingBase entity) {
      this.entity = entity;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }
}
