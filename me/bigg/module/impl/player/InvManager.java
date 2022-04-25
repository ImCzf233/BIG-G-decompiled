package me.bigg.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class InvManager extends Module {
   private final TimerUtil timer = new TimerUtil();
   private final ArrayList whitelistedItems = new ArrayList();
   private static final int weaponSlot = 36;
   private static final int pickaxeSlot = 37;
   private static final int axeSlot = 38;
   private static final int shovelSlot = 39;
   private final EnumValue modeSetting = new EnumValue("Mode", "Active mode", InvManager.modeEnums.values());
   private final NumValue dela = new NumValue("Delay", "Delay time of sorter.", 1.0, 0.0, 5.0, 0.1);
   private final BoolValue food = new BoolValue("Food", "Keep food?", true);
   private final BoolValue archery = new BoolValue("Archery", "Keep archery?", false);
   private final BoolValue uhc = new BoolValue("UHC", "Works in UHC?", false);

   public InvManager() {
      super("Inv Manager", "Auto sort your inventory for you.", Category.Player);
      this.addValues(new Value[]{this.modeSetting, this.dela, this.food, this.uhc, this.archery});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (!(mc.thePlayer.openContainer instanceof ContainerChest) || !(mc.currentScreen instanceof GuiContainer)) {
         long delay = ((Number)this.dela.getValue()).longValue() * 100L;
         if ((this.modeSetting.getMode() == InvManager.modeEnums.Vanilla || mc.currentScreen instanceof GuiInventory) && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat)) {
            if (this.timer.hasPassed(delay)) {
               if (!mc.thePlayer.inventoryContainer.getSlot(36).getHasStack()) {
                  this.getBestWeapon(36);
               } else if (!this.isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) {
                  this.getBestWeapon(36);
               }
            }

            if (this.timer.hasPassed(delay)) {
               this.getBestPickaxe(37);
            }

            if (this.timer.hasPassed(delay)) {
               this.getBestShovel(39);
            }

            if (this.timer.hasPassed(delay)) {
               this.getBestAxe(38);
            }

            if (this.timer.hasPassed(delay)) {
               for(int i = 9; i < 45; ++i) {
                  if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                     ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                     if (this.shouldDrop(is, i)) {
                        this.drop(i);
                        if (delay > 0L) {
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void shiftClick(int slot) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
   }

   public void swap(int slot1, int hotbarSlot) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
   }

   public void drop(int slot) {
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
      this.timer.reset();
   }

   public boolean isBestWeapon(ItemStack stack) {
      float damage = this.getDamage(stack);

      for(int i = 9; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (this.getDamage(is) > damage && is.getItem() instanceof ItemSword) {
               return false;
            }
         }
      }

      return stack.getItem() instanceof ItemSword;
   }

   public void getBestWeapon(int slot) {
      for(int i = 9; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (this.isBestWeapon(is) && this.getDamage(is) > 0.0F && is.getItem() instanceof ItemSword) {
               this.swap(i, slot - 36);
               this.timer.reset();
               break;
            }
         }
      }

   }

   private float getDamage(ItemStack stack) {
      float damage = 0.0F;
      Item item = stack.getItem();
      if (item instanceof ItemTool) {
         ItemTool sword = (ItemTool)item;
         damage += (float)sword.getMaxDamage();
      }

      if (item instanceof ItemSword) {
         ItemSword sword1 = (ItemSword)item;
         damage += sword1.getDamageVsEntity();
      }

      damage += (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F + (float)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01F;
      return damage;
   }

   public boolean shouldDrop(ItemStack stack, int slot) {
      if (stack.getDisplayName().toLowerCase().contains("(right click)")) {
         return false;
      } else {
         if ((Boolean)this.uhc.getValue()) {
            if (stack.getDisplayName().toLowerCase().contains("头")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("apple")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("head")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("gold")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("crafting table")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("stick")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("and") && stack.getDisplayName().toLowerCase().contains("ril")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("axe of perun")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("barbarian")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("bloodlust")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("dragonchest")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("dragon sword")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("dragon armor")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("excalibur")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("exodus")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("fusion armor")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("hermes boots")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("hide of leviathan")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("scythe")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("seven-league boots")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("shoes of vidar")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("apprentice")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("master")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("vorpal")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("enchanted")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("spiked")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("tarnhelm")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("philosopher")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("anvil")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("panacea")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("fusion")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("excalibur")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("backpack")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("hermes")) {
               return false;
            }

            if (stack.getDisplayName().toLowerCase().contains("barbarian")) {
               return false;
            }
         }

         if (slot == 36 && this.isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(36).getStack())) {
            return false;
         } else if (slot == 37 && this.isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(37).getStack())) {
            return false;
         } else if (slot == 38 && this.isBestAxe(mc.thePlayer.inventoryContainer.getSlot(38).getStack())) {
            return false;
         } else if (slot == 39 && this.isBestShovel(mc.thePlayer.inventoryContainer.getSlot(39).getStack())) {
            return false;
         } else {
            if (stack.getItem() instanceof ItemArmor) {
               for(int type = 1; type < 5; ++type) {
                  if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                     ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                     if (this.isBestArmor(is, type)) {
                        continue;
                     }
                  }

                  if (this.isBestArmor(stack, type)) {
                     return false;
                  }
               }
            }

            if (stack.getItem() instanceof ItemPotion && this.isBadPotion(stack)) {
               return true;
            } else if (stack.getItem() instanceof ItemFood && (Boolean)this.food.getValue() && !(stack.getItem() instanceof ItemAppleGold)) {
               return true;
            } else if (!(stack.getItem() instanceof ItemHoe) && !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemArmor)) {
               if ((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow")) && !(Boolean)this.archery.getValue()) {
                  return true;
               } else {
                  return stack.getItem().getUnlocalizedName().contains("tnt") || stack.getItem().getUnlocalizedName().contains("stick") || stack.getItem().getUnlocalizedName().contains("egg") || stack.getItem().getUnlocalizedName().contains("string") || stack.getItem().getUnlocalizedName().contains("cake") || stack.getItem().getUnlocalizedName().contains("mushroom") || stack.getItem().getUnlocalizedName().contains("flint") || stack.getItem().getUnlocalizedName().contains("compass") || stack.getItem().getUnlocalizedName().contains("dyePowder") || stack.getItem().getUnlocalizedName().contains("feather") || stack.getItem().getUnlocalizedName().contains("bucket") || stack.getItem().getUnlocalizedName().contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect") || stack.getItem().getUnlocalizedName().contains("snow") || stack.getItem().getUnlocalizedName().contains("fish") || stack.getItem().getUnlocalizedName().contains("enchant") || stack.getItem().getUnlocalizedName().contains("exp") || stack.getItem().getUnlocalizedName().contains("shears") || stack.getItem().getUnlocalizedName().contains("anvil") || stack.getItem().getUnlocalizedName().contains("torch") || stack.getItem().getUnlocalizedName().contains("seeds") || stack.getItem().getUnlocalizedName().contains("leather") || stack.getItem().getUnlocalizedName().contains("reeds") || stack.getItem().getUnlocalizedName().contains("skull") || stack.getItem().getUnlocalizedName().contains("record") || stack.getItem().getUnlocalizedName().contains("snowball") || stack.getItem() instanceof ItemGlassBottle || stack.getItem().getUnlocalizedName().contains("piston");
               }
            } else {
               return true;
            }
         }
      }
   }

   public ArrayList getWhitelistedItem() {
      return this.whitelistedItems;
   }

   private void getBestPickaxe(int slot) {
      for(int i = 9; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (this.isBestPickaxe(is) && 37 != i && !this.isBestWeapon(is)) {
               if (!mc.thePlayer.inventoryContainer.getSlot(37).getHasStack()) {
                  this.swap(i, 1);
                  this.timer.reset();
                  if (((Number)this.dela.getValue()).longValue() > 0L) {
                     return;
                  }
               } else if (!this.isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(37).getStack())) {
                  this.swap(i, 1);
                  this.timer.reset();
                  if (((Number)this.dela.getValue()).longValue() > 0L) {
                     return;
                  }
               }
            }
         }
      }

   }

   private void getBestShovel(int slot) {
      for(int i = 9; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (this.isBestShovel(is) && 39 != i && !this.isBestWeapon(is)) {
               if (!mc.thePlayer.inventoryContainer.getSlot(39).getHasStack()) {
                  this.swap(i, 3);
                  this.timer.reset();
                  if (((Number)this.dela.getValue()).longValue() > 0L) {
                     return;
                  }
               } else if (!this.isBestShovel(mc.thePlayer.inventoryContainer.getSlot(39).getStack())) {
                  this.swap(i, 3);
                  this.timer.reset();
                  if (((Number)this.dela.getValue()).longValue() > 0L) {
                     return;
                  }
               }
            }
         }
      }

   }

   private void getBestAxe(int slot) {
      for(int i = 9; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (this.isBestAxe(is) && 38 != i && !this.isBestWeapon(is)) {
               if (!mc.thePlayer.inventoryContainer.getSlot(38).getHasStack()) {
                  this.swap(i, 2);
                  this.timer.reset();
                  if (((Number)this.dela.getValue()).longValue() > 0L) {
                     return;
                  }
               } else if (!this.isBestAxe(mc.thePlayer.inventoryContainer.getSlot(38).getStack())) {
                  this.swap(i, 2);
                  this.timer.reset();
                  if (((Number)this.dela.getValue()).longValue() > 0L) {
                     return;
                  }
               }
            }
         }
      }

   }

   boolean isBestPickaxe(ItemStack stack) {
      Item item = stack.getItem();
      if (!(item instanceof ItemPickaxe)) {
         return false;
      } else {
         float value = this.getToolEffect(stack);

         for(int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
               if (this.getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   boolean isBestShovel(ItemStack stack) {
      Item item = stack.getItem();
      if (!(item instanceof ItemSpade)) {
         return false;
      } else {
         float value = this.getToolEffect(stack);

         for(int i = 9; i < 45; ++i) {
            Minecraft var10000 = mc;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               var10000 = mc;
               ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
               if (this.getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   boolean isBestAxe(ItemStack stack) {
      Item item = stack.getItem();
      if (!(item instanceof ItemAxe)) {
         return false;
      } else {
         float value = this.getToolEffect(stack);

         for(int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
               if (this.getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !this.isBestWeapon(stack)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   float getToolEffect(ItemStack stack) {
      Item item = stack.getItem();
      if (!(item instanceof ItemTool)) {
         return 0.0F;
      } else {
         String name = item.getUnlocalizedName();
         ItemTool tool = (ItemTool)item;
         float value = 1.0F;
         if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) {
               value -= 5.0F;
            }
         } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) {
               value -= 5.0F;
            }
         } else {
            if (!(item instanceof ItemAxe)) {
               return 1.0F;
            }

            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) {
               value -= 5.0F;
            }
         }

         value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075);
         value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0);
         return value;
      }
   }

   boolean isBadPotion(ItemStack stack) {
      if (stack != null && stack.getItem() instanceof ItemPotion) {
         ItemPotion potion = (ItemPotion)stack.getItem();
         if (potion.getEffects(stack) == null) {
            return true;
         }

         Iterator var4 = potion.getEffects(stack).iterator();

         while(var4.hasNext()) {
            Object o = var4.next();
            PotionEffect effect = (PotionEffect)o;
            if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
               return true;
            }
         }
      }

      return false;
   }

   boolean invContainsType(int type) {
      for(int i = 9; i < 45; ++i) {
         Minecraft var10000 = mc;
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            var10000 = mc;
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if (item instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)item;
               if (type == armor.armorType) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   boolean isBestArmor(ItemStack stack, int type) {
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

   float getProtection(ItemStack stack) {
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
