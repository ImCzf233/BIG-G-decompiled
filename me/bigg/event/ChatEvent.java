package me.bigg.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.util.IChatComponent;

public class ChatEvent extends EventCancellable {
   private final IChatComponent chatComponent;

   public ChatEvent(IChatComponent icc) {
      this.chatComponent = icc;
   }

   public IChatComponent getChatComponent() {
      return this.chatComponent;
   }
}
