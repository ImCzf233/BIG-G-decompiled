package me.bigg.module.impl.misc.scaffoldUtils;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.util.DamageSource;

public class InventoryUtils {
   public static Minecraft mc = Minecraft.getMinecraft();
   public static final List BLOCK_BLACKLIST;

   static {
      BLOCK_BLACKLIST = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch, Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.red_flower, Blocks.flower_pot, Blocks.yellow_flower, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner, Blocks.wall_banner);
   }

   public static int findAutoBlockBlock() {
      for(int i = 36; i < 45; ++i) {
         ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
         if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock)itemStack.getItem();
            Block block = itemBlock.getBlock();
            if (block.isFullCube() && !BLOCK_BLACKLIST.contains(block)) {
               return i;
            }
         }
      }

      return -1;
   }

   public static boolean isBlockListBlock(ItemBlock itemBlock) {
      Block block = itemBlock.getBlock();
      return BLOCK_BLACKLIST.contains(block) || !block.isFullCube();
   }

   public void dropSlot(int slot) {
      int windowId = (new GuiInventory(mc.thePlayer)).inventorySlots.windowId;
      mc.playerController.windowClick(windowId, slot, 1, 4, mc.thePlayer);
   }

   public static void updateInventory() {
      for(int index = 0; index < 44; ++index) {
         try {
            int offset = index < 9 ? 36 : 0;
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C10PacketCreativeInventoryAction(index + offset, Minecraft.getMinecraft().thePlayer.inventory.mainInventory[index]));
         } catch (Exception var2) {
         }
      }

   }

   public static ItemStack getStackInSlot(int slot) {
      return mc.thePlayer.inventory.getStackInSlot(slot);
   }

   public static boolean isBestArmorOfTypeInInv(ItemStack is) {
      try {
         if (is == null) {
            return false;
         }

         if (is.getItem() == null) {
            return false;
         }

         if (is.getItem() != null && !(is.getItem() instanceof ItemArmor)) {
            return false;
         }

         ItemArmor ia = (ItemArmor)is.getItem();
         int prot = getArmorProt(is);

         ItemStack stack;
         ItemArmor otherArmor;
         int i;
         for(i = 0; i < 4; ++i) {
            stack = mc.thePlayer.inventory.armorInventory[i];
            if (stack != null) {
               otherArmor = (ItemArmor)stack.getItem();
               if (otherArmor.armorType == ia.armorType && getArmorProt(stack) >= prot) {
                  return false;
               }
            }
         }

         for(i = 0; i < mc.thePlayer.inventory.getSizeInventory() - 4; ++i) {
            stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemArmor) {
               otherArmor = (ItemArmor)stack.getItem();
               if (otherArmor.armorType == ia.armorType && otherArmor != ia && getArmorProt(stack) >= prot) {
                  return false;
               }
            }
         }
      } catch (Exception var7) {
      }

      return true;
   }

   public static boolean hotbarHas(Item item) {
      for(int index = 0; index <= 36; ++index) {
         ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
         if (stack != null && stack.getItem() == item) {
            return true;
         }
      }

      return false;
   }

   public static boolean hotbarHas(Item item, int slotID) {
      for(int index = 0; index <= 36; ++index) {
         ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
         if (stack != null && stack.getItem() == item && getSlotID(stack.getItem()) == slotID) {
            return true;
         }
      }

      return false;
   }

   public static int getSlotID(Item item) {
      for(int index = 0; index <= 36; ++index) {
         ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
         if (stack != null && stack.getItem() == item) {
            return index;
         }
      }

      return -1;
   }

   public static ItemStack getItemBySlotID(int slotID) {
      for(int index = 0; index <= 36; ++index) {
         ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(index);
         if (stack != null && getSlotID(stack.getItem()) == slotID) {
            return stack;
         }
      }

      return null;
   }

   public static int getArmorProt(ItemStack i) {
      int armorprot = -1;
      if (i != null && i.getItem() != null && i.getItem() instanceof ItemArmor) {
         armorprot = ((ItemArmor)i.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(i)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{i}, DamageSource.generic);
      }

      return armorprot;
   }

   public boolean isBestChest(int slot) {
      if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
         int slotProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
         ItemArmor ia1;
         if (mc.thePlayer.inventory.armorInventory[2] != null) {
            ItemArmor ia = (ItemArmor)mc.thePlayer.inventory.armorInventory[2].getItem();
            ItemStack is = mc.thePlayer.inventory.armorInventory[2];
            ia1 = (ItemArmor)getStackInSlot(slot).getItem();
            int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
            if (otherProtection > slotProtection || otherProtection == slotProtection) {
               return false;
            }
         }

         for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); ++i) {
            if (getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
               int otherProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
               ia1 = (ItemArmor)getStackInSlot(slot).getItem();
               ItemArmor ia2 = (ItemArmor)getStackInSlot(i).getItem();
               if (ia1.armorType == 1 && ia2.armorType == 1 && otherProtection > slotProtection) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public boolean isBestHelmet(int slot) {
      if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
         int slotProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
         ItemArmor ia1;
         if (mc.thePlayer.inventory.armorInventory[3] != null) {
            ItemArmor ia = (ItemArmor)mc.thePlayer.inventory.armorInventory[3].getItem();
            ItemStack is = mc.thePlayer.inventory.armorInventory[3];
            ia1 = (ItemArmor)getStackInSlot(slot).getItem();
            int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
            if (otherProtection > slotProtection || otherProtection == slotProtection) {
               return false;
            }
         }

         for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); ++i) {
            if (getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
               int otherProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
               ia1 = (ItemArmor)getStackInSlot(slot).getItem();
               ItemArmor ia2 = (ItemArmor)getStackInSlot(i).getItem();
               if (ia1.armorType == 0 && ia2.armorType == 0 && otherProtection > slotProtection) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public boolean isBestLeggings(int slot) {
      if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
         int slotProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
         ItemArmor ia1;
         if (mc.thePlayer.inventory.armorInventory[1] != null) {
            ItemArmor ia = (ItemArmor)mc.thePlayer.inventory.armorInventory[1].getItem();
            ItemStack is = mc.thePlayer.inventory.armorInventory[1];
            ia1 = (ItemArmor)getStackInSlot(slot).getItem();
            int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
            if (otherProtection > slotProtection || otherProtection == slotProtection) {
               return false;
            }
         }

         for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); ++i) {
            if (getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
               int otherProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
               ia1 = (ItemArmor)getStackInSlot(slot).getItem();
               ItemArmor ia2 = (ItemArmor)getStackInSlot(i).getItem();
               if (ia1.armorType == 2 && ia2.armorType == 2 && otherProtection > slotProtection) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public boolean isBestBoots(int slot) {
      if (getStackInSlot(slot) != null && getStackInSlot(slot).getItem() != null && getStackInSlot(slot).getItem() instanceof ItemArmor) {
         int slotProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(slot).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(slot))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(slot)}, DamageSource.generic);
         ItemArmor ia1;
         if (mc.thePlayer.inventory.armorInventory[0] != null) {
            ItemArmor ia = (ItemArmor)mc.thePlayer.inventory.armorInventory[0].getItem();
            ItemStack is = mc.thePlayer.inventory.armorInventory[0];
            ia1 = (ItemArmor)getStackInSlot(slot).getItem();
            int otherProtection = ((ItemArmor)is.getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(is)) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{is}, DamageSource.generic);
            if (otherProtection > slotProtection || otherProtection == slotProtection) {
               return false;
            }
         }

         for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); ++i) {
            if (getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemArmor) {
               int otherProtection = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
               ia1 = (ItemArmor)getStackInSlot(slot).getItem();
               ItemArmor ia2 = (ItemArmor)getStackInSlot(i).getItem();
               if (ia1.armorType == 3 && ia2.armorType == 3 && otherProtection > slotProtection) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static int getItemType(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemArmor) {
         ItemArmor armor = (ItemArmor)itemStack.getItem();
         return armor.armorType;
      } else {
         return -1;
      }
   }

   public static float getItemDamage(ItemStack itemStack) {
      Multimap multimap = itemStack.getAttributeModifiers();
      Iterator iterator;
      if (!multimap.isEmpty() && (iterator = multimap.entries().iterator()).hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
         double damage = attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2 ? attributeModifier.getAmount() : attributeModifier.getAmount() * 100.0;
         return attributeModifier.getAmount() > 1.0 ? 1.0F + (float)damage : 1.0F;
      } else {
         return 1.0F;
      }
   }

   public boolean hasItemMoreTimes(int slotIn) {
      boolean has = false;
      ArrayList stacks = new ArrayList();
      stacks.clear();

      for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); ++i) {
         if (!stacks.contains(getStackInSlot(i))) {
            stacks.add(getStackInSlot(i));
         } else if (getStackInSlot(i) == getStackInSlot(slotIn)) {
            return true;
         }
      }

      return false;
   }

   public int getArmorProt(int i) {
      int armorprot = -1;
      if (getStackInSlot(i) != null && getStackInSlot(i).getItem() != null && getStackInSlot(i).getItem() instanceof ItemArmor) {
         armorprot = ((ItemArmor)mc.thePlayer.inventory.getStackInSlot(i).getItem()).getArmorMaterial().getDamageReductionAmount(getItemType(mc.thePlayer.inventory.getStackInSlot(i))) + EnchantmentHelper.getEnchantmentModifierDamage(new ItemStack[]{mc.thePlayer.inventory.getStackInSlot(i)}, DamageSource.generic);
      }

      return armorprot;
   }

   public static int getFirstItem(Item i1) {
      for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); ++i) {
         if (getStackInSlot(i) != null && getStackInSlot(i).getItem() != null && getStackInSlot(i).getItem() == i1) {
            return i;
         }
      }

      return -1;
   }

   public static boolean isBestSword(ItemStack itemSword, int slot) {
      if (itemSword != null && itemSword.getItem() instanceof ItemSword) {
         for(int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); ++i) {
            ItemStack iStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (iStack != null && iStack.getItem() instanceof ItemSword && getItemDamage(iStack) >= getItemDamage(itemSword) && slot != i) {
               return false;
            }
         }
      }

      return true;
   }

   public static int findItem(int startSlot, int endSlot, Item item) {
      for(int i = startSlot; i < endSlot; ++i) {
         ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
         if (stack != null && stack.getItem() == item) {
            return i;
         }
      }

      return -1;
   }
}
