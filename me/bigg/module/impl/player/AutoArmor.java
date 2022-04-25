package me.bigg.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.TimerUtil;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class AutoArmor extends Module {
   private final TimerUtil timer = new TimerUtil();
   private final EnumValue modeSetting = new EnumValue("Mode", "Active mode", AutoArmor.modeEnums.values());
   private final NumValue delayValue = new NumValue("Delay", "Pick delay", 1.0, 0.0, 5.0, 0.1);

   public AutoArmor() {
      super("Auto Armor", "Auto armor for you.", Category.Player);
      this.addValues(new Value[]{this.modeSetting, this.delayValue});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         long delay = (long)((Number)this.delayValue.getValue()).intValue() * 100L;
         if (this.modeSetting.getMode() == AutoArmor.modeEnums.OpenInv && !(mc.currentScreen instanceof GuiInventory)) {
            return;
         }

         if ((mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) && this.timer.hasPassed(delay)) {
            for(int type = 1; type < 5; ++type) {
               if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                  ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                  if (this.isBestArmor(is, type)) {
                     continue;
                  }

                  if (this.modeSetting.getMode() == AutoArmor.modeEnums.Vanilla) {
                     mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                  }

                  this.drop(4 + type);
               }

               for(int i = 9; i < 45; ++i) {
                  if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                     ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                     if (this.isBestArmor(is, type) && this.getProtection(is) > 0.0F) {
                        this.shiftClick(i);
                        this.timer.reset();
                        if (((Number)this.delayValue.getValue()).longValue() > 0L) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private boolean isBestArmor(ItemStack stack, int type) {
      float prot = this.getProtection(stack);
      String strType = "";
      if (type == 1) {
         strType = "helmet";
      } else if (type == 2) {
         strType = "chestplate";
      } else if (type == 3) {
         strType = "leggings";
      } else if (type == 4) {
         strType = "boots";
      }

      if (!stack.getUnlocalizedName().contains(strType)) {
         return false;
      } else {
         for(int i = 5; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
               if (this.getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private void shiftClick(int slot) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
   }

   private void drop(int slot) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
   }

   private float getProtection(ItemStack stack) {
      float prot = 0.0F;
      if (stack.getItem() instanceof ItemArmor) {
         ItemArmor armor = (ItemArmor)stack.getItem();
         prot = (float)((double)prot + (double)armor.damageReduceAmount + (double)((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075);
         prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0);
         prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0);
         prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0);
         prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0);
         prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0);
      }

      return prot;
   }

   private static enum modeEnums {
      Vanilla,
      OpenInv;
   }
}
