package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldReloadEvent implements Event {
   private final WorldClient world;

   public WorldReloadEvent(WorldClient world) {
      this.world = world;
   }

   public WorldClient getWorld() {
      return this.world;
   }
}
