package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Module {
   public FullBright() {
      super("Full Bright", "Just full bright", Category.Vision);
   }

   public void onEnable() {
      super.onEnable();
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 5200, 1));
   }

   public void onDisable() {
      super.onDisable();
      mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
   }
}
