package me.bigg.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderCapeEvent extends EventCancellable {
   private ResourceLocation capeLocation;
   private final EntityPlayer player;

   public RenderCapeEvent(ResourceLocation capeLocation, EntityPlayer player) {
      this.capeLocation = capeLocation;
      this.player = player;
   }

   public ResourceLocation getLocation() {
      return this.capeLocation;
   }

   public void setLocation(ResourceLocation location) {
      this.capeLocation = location;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }
}
