package me.bigg.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.network.Packet;

public class PacketEvent extends EventCancellable {
   private Packet packet;
   private final boolean outGoing;

   public PacketEvent(Packet packet, boolean outGoing) {
      this.packet = packet;
      this.outGoing = outGoing;
   }

   public Packet getPacket() {
      return this.packet;
   }

   public void setPacket(Packet packet) {
      this.packet = packet;
   }

   public boolean isOutGoing() {
      return this.outGoing;
   }

   public boolean isInComing() {
      return !this.outGoing;
   }
}
