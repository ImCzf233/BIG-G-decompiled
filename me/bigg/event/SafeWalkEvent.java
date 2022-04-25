package me.bigg.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class SafeWalkEvent extends EventCancellable {
   public SafeWalkEvent(boolean safeWalking) {
      this.setCancelled(safeWalking);
   }
}
