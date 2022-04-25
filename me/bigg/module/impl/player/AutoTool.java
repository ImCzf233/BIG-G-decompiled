package me.bigg.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class AutoTool extends Module {
   public AutoTool() {
      super("Auto Tool", "Auto swap tool", Category.Player);
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (mc.gameSettings.keyBindAttack.pressed && !mc.thePlayer.isUsingItem() && mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null) {
         Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
         float strength = 1.0F;
         int bestItemIndex = -1;

         for(int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && !(itemStack.getStrVsBlock(block) <= strength)) {
               strength = itemStack.getStrVsBlock(block);
               bestItemIndex = i;
            }
         }

         if (bestItemIndex != -1) {
            mc.thePlayer.inventory.currentItem = bestItemIndex;
         }

      }
   }
}
