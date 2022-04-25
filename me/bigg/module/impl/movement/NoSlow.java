package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.combat.KillAura;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.Value;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;

public class NoSlow extends Module {
   private final BoolValue packetValue = new BoolValue("Packet", "Packet no slow", true);
   private final BoolValue packetValuea = new BoolValue("Hypixel Eat", "Packet no slow", true);
   private final TimerUtil timer = new TimerUtil();

   public NoSlow() {
      super("No Slow", "Remove slowdown", Category.Movement);
      this.addValues(new Value[]{this.packetValuea, this.packetValue});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel((Boolean)this.packetValue.getValue() ? ((Boolean)this.packetValuea.getValue() ? "Watchdog" : "Packet") : "Vanilla");
      if (event.isPre() && (Boolean)this.packetValuea.getValue() && this.isUsingFood() && mc.thePlayer.getItemInUseDuration() >= 1) {
         mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      }

      if (KillAura.getBlockTarget() == null && (Boolean)this.packetValue.getValue() && mc.thePlayer.isBlocking() && PlayerUtil.MovementInput() && PlayerUtil.isOnGround(0.42)) {
         if (event.isPost()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
         }

      }
   }

   private boolean isUsingFood() {
      Item usingItem = mc.thePlayer.getItemInUse().getItem();
      return mc.thePlayer.isUsingItem() && (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion);
   }
}
