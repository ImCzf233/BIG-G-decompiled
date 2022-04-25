package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.Value;
import net.minecraft.potion.Potion;

public class Sprint extends Module {
   private final BoolValue AllDirectionValue = new BoolValue("All Direction", "Sprint all directions", false);

   public Sprint() {
      super("Sprint", "Auto sprint", Category.Movement);
      this.addValues(new Value[]{this.AllDirectionValue});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         boolean ward = (Boolean)this.AllDirectionValue.getValue() ? PlayerUtil.MovementInput() : mc.thePlayer.moveForward > 0.0F;
         if (ward && !mc.thePlayer.isSneaking() && (mc.thePlayer.getFoodStats().getFoodLevel() > 6 && !mc.thePlayer.isPotionActive(Potion.blindness) || mc.playerController.isInCreativeMode())) {
            mc.thePlayer.setSprinting(true);
         }

      }
   }
}
