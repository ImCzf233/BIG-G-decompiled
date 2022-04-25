package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class TimeChanger extends Module {
   private final BoolValue cyc = new BoolValue("Cycle", "cycle", false);
   private final NumValue speed = new NumValue("Cycle Speed", "Cycle speed.", 7.0, 1.0, 10.0, 1.0);
   private final NumValue time = new NumValue("Time", "Custom time.", 9000.0, 0.0, 24000.0, 1000.0);
   private long cyclopes;

   public TimeChanger() {
      super("Time Changer", "Change world time.", Category.Vision);
      this.addValues(new Value[]{this.speed, this.time, this.cyc});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if ((Boolean)this.cyc.getValue()) {
         this.cyclopes += ((Number)this.speed.getValue()).longValue() * 100L;
         mc.theWorld.setTotalWorldTime(this.cyclopes - 1L);
         mc.theWorld.setWorldTime(this.cyclopes - 1L);
      } else {
         mc.theWorld.setTotalWorldTime(((Number)this.time.getValue()).longValue() - 1L);
         mc.theWorld.setWorldTime(((Number)this.time.getValue()).longValue() - 1L);
      }

   }

   @EventTarget
   void onPacket(PacketEvent event) {
      Packet packet = event.getPacket();
      if (event.isInComing() && packet instanceof S03PacketTimeUpdate) {
         S03PacketTimeUpdate s03PacketTimeUpdate = (S03PacketTimeUpdate)packet;
         s03PacketTimeUpdate.totalWorldTime = (Boolean)this.cyc.getValue() ? this.cyclopes - 1L : ((Number)this.time.getValue()).longValue() - 1L;
         s03PacketTimeUpdate.worldTime = (Boolean)this.cyc.getValue() ? this.cyclopes - 1L : ((Number)this.time.getValue()).longValue() - 1L;
      }

   }
}
