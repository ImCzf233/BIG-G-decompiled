package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;

public class KeyTypedEvent implements Event {
   private final int key;

   public KeyTypedEvent(int keyCode) {
      this.key = keyCode;
   }

   public int getKey() {
      return this.key;
   }
}
