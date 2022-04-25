package me.bigg.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ChestStealer extends Module {
   private final NumValue dela = new NumValue("Delay", "Delay time of sorter.", 1.0, 0.0, 5.0, 0.1);
   private static final BoolValue silentValue = new BoolValue("Silent", "Silent Stealer", true);
   private static final BoolValue checkValue = new BoolValue("Check", "Check Chest", true);
   private final TimerUtil timer = new TimerUtil();

   public ChestStealer() {
      super("Chest Stealer", "Steal chest for you.", Category.Player);
      this.addValues(new Value[]{this.dela, checkValue, silentValue});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (event.isPre() && mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
         ContainerChest container = (ContainerChest)mc.thePlayer.openContainer;
         if ((Boolean)checkValue.getValue()) {
            String name = container.getLowerChestInventory().getDisplayName().getUnformattedText().toLowerCase();
            String[] list = new String[]{"menu", "selector", "game", "gui", "server", "inventory", "play", "teleporter", "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept", "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock", "anticheat", "travel", "settings", "user", "preference", "compass", "cake", "wars", "buy", "upgrade", "ranged", "potions", "utility"};
            String[] var8 = list;
            int var7 = list.length;

            for(int var6 = 0; var6 < var7; ++var6) {
               String str = var8[var6];
               if (name.contains(str)) {
                  return;
               }
            }
         }

         for(int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
            if (container.getLowerChestInventory().getStackInSlot(i) != null && this.timer.hasPassed(((Number)this.dela.getValue()).longValue() * 100L) && (!(container.getLowerChestInventory().getStackInSlot(i).getItem() instanceof ItemArmor) || this.betterCheck(container, container.getLowerChestInventory().getStackInSlot(i), i)) && (!(container.getLowerChestInventory().getStackInSlot(i).getItem() instanceof ItemSword) || (double)this.getDamage(container.getLowerChestInventory().getStackInSlot(i)) >= this.bestDamage(container, i))) {
               mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
               this.timer.reset();
            }
         }

         if (this.isEmpty()) {
            mc.thePlayer.closeScreen();
         }
      }

   }

   private boolean isEmpty() {
      if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
         ContainerChest container = (ContainerChest)mc.thePlayer.openContainer;

         for(int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
            ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() != null && (!(itemStack.getItem() instanceof ItemArmor) || this.betterCheck(container, itemStack, i)) && (!(itemStack.getItem() instanceof ItemSword) || (double)this.getDamage(itemStack) >= this.bestDamage(container, i))) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean betterCheck(ContainerChest c, ItemStack item, int slot) {
      double item1 = (double)((ItemArmor)item.getItem()).damageReduceAmount + this.getProtectionValue(item);
      double item2 = 0.0;
      int bestslot = 0;
      int i;
      double temp;
      double var12;
      if (item.getUnlocalizedName().contains("helmet")) {
         for(i = 0; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem().getUnlocalizedName().contains("helmet")) {
               var12 = (double)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
               temp = var12 + this.getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }

         for(i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null && c.getLowerChestInventory().getStackInSlot(i).getUnlocalizedName().contains("helmet")) {
               temp = (double)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount + this.getProtectionValue(c.getLowerChestInventory().getStackInSlot(i));
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }
      }

      if (item.getUnlocalizedName().contains("chestplate")) {
         for(i = 0; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem().getUnlocalizedName().contains("chestplate")) {
               var12 = (double)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
               temp = var12 + this.getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }

         for(i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null && c.getLowerChestInventory().getStackInSlot(i).getUnlocalizedName().contains("chestplate")) {
               temp = (double)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount + this.getProtectionValue(c.getLowerChestInventory().getStackInSlot(i));
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }
      }

      if (item.getUnlocalizedName().contains("leggings")) {
         for(i = 0; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem().getUnlocalizedName().contains("leggings")) {
               var12 = (double)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
               temp = var12 + this.getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }

         for(i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null && c.getLowerChestInventory().getStackInSlot(i).getUnlocalizedName().contains("leggings")) {
               temp = (double)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount + this.getProtectionValue(c.getLowerChestInventory().getStackInSlot(i));
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }
      }

      if (item.getUnlocalizedName().contains("boots")) {
         for(i = 0; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem().getUnlocalizedName().contains("boots")) {
               var12 = (double)((ItemArmor)mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem()).damageReduceAmount;
               temp = var12 + this.getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).getStack());
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }

         for(i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) != null && c.getLowerChestInventory().getStackInSlot(i).getUnlocalizedName().contains("boots")) {
               temp = (double)((ItemArmor)c.getLowerChestInventory().getStackInSlot(i).getItem()).damageReduceAmount + this.getProtectionValue(c.getLowerChestInventory().getStackInSlot(i));
               if (temp > item2) {
                  item2 = temp;
                  bestslot = i;
               }
            }
         }
      }

      return item1 >= item2 && c.getLowerChestInventory().getStackInSlot(bestslot) == item;
   }

   private double getProtectionValue(ItemStack stack) {
      return stack.getItem() instanceof ItemArmor ? (double)((ItemArmor)stack.getItem()).damageReduceAmount + (double)((100 - ((ItemArmor)stack.getItem()).damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075 : 0.0;
   }

   private double bestDamage(ContainerChest container, int slot) {
      double bestDamage = 0.0;

      int i;
      ItemStack is;
      for(i = 0; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is.getItem() instanceof ItemSword && (double)this.getDamage(is) > bestDamage) {
               bestDamage = (double)this.getDamage(is);
            }
         }
      }

      for(i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
         if (container.getLowerChestInventory().getStackInSlot(i) != null) {
            is = container.getLowerChestInventory().getStackInSlot(i);
            if (i != slot && is.getItem() instanceof ItemSword && (double)this.getDamage(is) > bestDamage) {
               bestDamage = (double)this.getDamage(is);
            }
         }
      }

      return bestDamage;
   }

   private float getDamage(ItemStack stack) {
      float damage = 0.0F;
      Item item = stack.getItem();
      if (item instanceof ItemTool) {
         damage += this.getSpeed(stack);
      }

      if (item instanceof ItemSword) {
         damage += this.getAttackDamage(stack);
      } else {
         ++damage;
      }

      return damage;
   }

   private float getAttackDamage(ItemStack itemStack) {
      float damage = ((ItemSword)itemStack.getItem()).getDamageVsEntity();
      damage += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
      damage += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.01F;
      return damage;
   }

   private float getSpeed(ItemStack stack) {
      return ((ItemTool)stack.getItem()).getToolMaterial().getEfficiencyOnProperMaterial();
   }

   public static BoolValue getSilentValue() {
      return silentValue;
   }

   public static BoolValue getCheckValue() {
      return checkValue;
   }
}
