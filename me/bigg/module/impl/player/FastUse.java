package me.bigg.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class FastUse extends Module {
   private final EnumValue modeValue = new EnumValue("Mode", "Use mode", FastUse.modeEnums.values());
   public NumValue tickValue = new NumValue("Tick", "Eat tick", 12.0, 1.0, 20.0, 1.0);

   public FastUse() {
      super("Fast Use", "Use item instant", Category.Player);
      this.addValues(new Value[]{this.modeValue, this.tickValue});
   }

   @EventTarget
   void onUpdate(UpdateEvent updateEvent) {
      this.setLabel(this.modeValue.getMode().name());
      if (updateEvent.isPre()) {
         if (this.isUsingFood()) {
            if (this.modeValue.getMode() == FastUse.modeEnums.NCP && mc.thePlayer.getItemInUseDuration() >= ((Number)this.tickValue.getValue()).intValue()) {
               for(int i = 0; i < 39; ++i) {
                  mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
               }

               mc.thePlayer.stopUsingItem();
            }

            if (this.modeValue.getMode() == FastUse.modeEnums.Timer && mc.thePlayer.getItemInUseDuration() >= ((Number)this.tickValue.getValue()).intValue()) {
               mc.timer.timerSpeed = 1.35557F;
            } else if (mc.timer.timerSpeed == 1.35557F) {
               mc.timer.timerSpeed = 1.0F;
            }

            if (this.modeValue.getMode() == FastUse.modeEnums.Hypixel && mc.thePlayer.getItemInUseDuration() >= 1) {
               mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
         }

      }
   }

   private boolean isUsingFood() {
      Item usingItem = mc.thePlayer.getItemInUse().getItem();
      return mc.thePlayer.isUsingItem() && (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion);
   }

   private static enum modeEnums {
      NCP,
      Hypixel,
      Timer;
   }
}
