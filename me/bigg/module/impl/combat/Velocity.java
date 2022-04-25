package me.bigg.module.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.PacketEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {
   private final NumValue amplifierValue = new NumValue("Amount", "Knock back amount.", 0.0, 0.0, 100.0, 5.0);

   public Velocity() {
      super("Velocity", "Anti server velocity", Category.Combat);
      this.addValues(new Value[]{this.amplifierValue});
   }

   @EventTarget
   void onPacket(PacketEvent event) {
      this.setLabel("Normal");
      Packet p = event.getPacket();
      if (event.isInComing()) {
         if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
               if (((Number)this.amplifierValue.getValue()).doubleValue() == 0.0) {
                  event.setCancelled(true);
               } else {
                  packet.motionX *= ((Number)this.amplifierValue.getValue()).intValue() / 100;
                  packet.motionY *= ((Number)this.amplifierValue.getValue()).intValue() / 100;
                  packet.motionZ *= ((Number)this.amplifierValue.getValue()).intValue() / 100;
               }
            }
         }

         if (event.getPacket() instanceof S27PacketExplosion) {
            S27PacketExplosion packet = (S27PacketExplosion)event.getPacket();
            if (((Number)this.amplifierValue.getValue()).doubleValue() == 0.0) {
               event.setCancelled(true);
            } else {
               packet.motionX *= (float)((Number)this.amplifierValue.getValue()).intValue() / 100.0F;
               packet.motionY *= (float)((Number)this.amplifierValue.getValue()).intValue() / 100.0F;
               packet.motionZ *= (float)((Number)this.amplifierValue.getValue()).intValue() / 100.0F;
            }
         }
      }

   }
}
