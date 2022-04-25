package me.bigg.module.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Iterator;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.InventoryUtil;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AutoPot extends Module {
   private static boolean potting;
   private int slot;
   private int last;
   private final TimerUtil timer = new TimerUtil();
   private final BoolValue healthValue = new BoolValue("Health", "Health Pot", true);
   private final BoolValue speedValue = new BoolValue("Speed", "Speed Pot", true);
   private final BoolValue jumpValue = new BoolValue("Jump", "Jump and pot", true);
   private final NumValue potHealthValue = new NumValue("Pot Health", "Health to health pot", 15.0, 3.0, 20.0, 1.0);

   public AutoPot() {
      super("Auto Pot", "Pot for u my master", Category.Combat);
      this.addValues(new Value[]{this.healthValue, this.speedValue, this.jumpValue, this.potHealthValue});
   }

   public void onEnable() {
      super.onEnable();
      potting = false;
      this.slot = -1;
      this.last = -1;
      this.timer.reset();
   }

   public void onDisable() {
      super.onDisable();
      potting = false;
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.slot = this.getSlot();
      if (event.isPre() && this.timer.hasPassed(1000L)) {
         int regenId = Potion.regeneration.getId();
         int speedId;
         if (!mc.thePlayer.isPotionActive(regenId) && !potting && mc.thePlayer.onGround && (Boolean)this.healthValue.getValue() && (double)mc.thePlayer.getHealth() <= ((Number)this.potHealthValue.getValue()).doubleValue() && this.hasPot(regenId)) {
            speedId = this.hasPot(regenId, this.slot);
            if (speedId != -1) {
               InventoryUtil.swap(speedId, this.slot);
            }

            this.last = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = this.slot;
            event.setPitch((float)((Boolean)this.jumpValue.getValue() && !PlayerUtil.MovementInput() ? -90 : (PlayerUtil.MovementInput() ? 85 : 90)));
            if ((Boolean)this.jumpValue.getValue() && !PlayerUtil.MovementInput()) {
               mc.thePlayer.motionY = 0.41999998688698 + (double)PlayerUtil.getJumpEffect() * 0.1;
               PlayerUtil.setSpeed(0.0);
            }

            potting = true;
            this.timer.reset();
         }

         speedId = Potion.moveSpeed.getId();
         if (!mc.thePlayer.isPotionActive(speedId) && !potting && mc.thePlayer.onGround && (Boolean)this.speedValue.getValue() && this.hasPot(speedId)) {
            int cum = this.hasPot(speedId, this.slot);
            if (cum != -1) {
               InventoryUtil.swap(cum, this.slot);
            }

            this.last = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = this.slot;
            event.setPitch((float)((Boolean)this.jumpValue.getValue() && !PlayerUtil.MovementInput() ? -90 : (PlayerUtil.MovementInput() ? 85 : 90)));
            if ((Boolean)this.jumpValue.getValue() && !PlayerUtil.MovementInput()) {
               mc.thePlayer.motionY = 0.41999998688698 + (double)PlayerUtil.getJumpEffect() * 0.1;
               PlayerUtil.setSpeed(0.0);
            }

            potting = true;
            this.timer.reset();
         }
      }

      if (event.isPost() && potting) {
         if (mc.thePlayer.inventory.getCurrentItem() != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
            mc.entityRenderer.itemRenderer.resetEquippedProgress2();
         }

         if (this.last != -1) {
            mc.thePlayer.inventory.currentItem = this.last;
         }

         potting = false;
         this.last = -1;
      }

   }

   private int hasPot(int id, int targetSlot) {
      for(int i = 9; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is.getItem() instanceof ItemPotion) {
               ItemPotion pot = (ItemPotion)is.getItem();
               if (!pot.getEffects(is).isEmpty()) {
                  PotionEffect effect = (PotionEffect)pot.getEffects(is).get(0);
                  if (effect.getPotionID() == id && ItemPotion.isSplash(is.getItemDamage()) && this.isBestPot(pot, is) && 36 + targetSlot != i) {
                     return i;
                  }
               }
            }
         }
      }

      return -1;
   }

   private boolean hasPot(int id) {
      for(int i = 9; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is.getItem() instanceof ItemPotion) {
               ItemPotion pot = (ItemPotion)is.getItem();
               if (!pot.getEffects(is).isEmpty()) {
                  PotionEffect effect = (PotionEffect)pot.getEffects(is).get(0);
                  if (effect.getPotionID() == id && ItemPotion.isSplash(is.getItemDamage()) && this.isBestPot(pot, is)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean isBestPot(ItemPotion potion, ItemStack stack) {
      if (potion.getEffects(stack) != null && potion.getEffects(stack).size() == 1) {
         PotionEffect effect = (PotionEffect)potion.getEffects(stack).get(0);
         int potionID = effect.getPotionID();
         int amplifier = effect.getAmplifier();
         int duration = effect.getDuration();

         for(int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
               ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
               if (is.getItem() instanceof ItemPotion) {
                  ItemPotion pot = (ItemPotion)is.getItem();
                  if (pot.getEffects(is) != null) {
                     Iterator var11 = pot.getEffects(is).iterator();

                     while(var11.hasNext()) {
                        Object o = var11.next();
                        PotionEffect effects = (PotionEffect)o;
                        int id = effects.getPotionID();
                        int ampl = effects.getAmplifier();
                        int dur = effects.getDuration();
                        if (id == potionID && ItemPotion.isSplash(is.getItemDamage())) {
                           if (ampl > amplifier) {
                              return false;
                           }

                           if (ampl == amplifier && dur > duration) {
                              return false;
                           }
                        }
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private int getSlot() {
      int spoofSlot = 8;

      for(int i = 36; i < 45; ++i) {
         if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            spoofSlot = i - 36;
            break;
         }

         if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
            spoofSlot = i - 36;
            break;
         }
      }

      return spoofSlot;
   }

   public static boolean isPotting() {
      return potting;
   }
}
