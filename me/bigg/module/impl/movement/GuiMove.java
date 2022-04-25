package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.PacketEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.Value;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class GuiMove extends Module {
   private final BoolValue noMoveClick = new BoolValue("NoMoveClick", "ss", true);
   private final BoolValue hypixel = new BoolValue("Hypixel", "bypass hypixel", true);

   public GuiMove() {
      super("Gui Move", "Move when has gui", Category.Movement);
      this.addValues(new Value[]{this.noMoveClick, this.hypixel});
   }

   @EventTarget
   void onPacket(PacketEvent e) {
      if (e.isOutGoing() && e.getPacket() instanceof C0EPacketClickWindow && PlayerUtil.MovementInput() && (Boolean)this.noMoveClick.getValue()) {
         e.setCancelled(true);
      }

      if (e.isInComing() && e.getPacket() instanceof S08PacketPlayerPosLook && (Boolean)this.hypixel.getValue()) {
         e.setCancelled(mc.thePlayer.ticksExisted % 4 == 0);
      }

   }
}
