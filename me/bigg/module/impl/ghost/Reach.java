package me.bigg.module.impl.ghost;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;

public class Reach extends Module {
   public final BoolValue interact = new BoolValue("Block Interact", "Interact block distance", true);
   public final NumValue range = new NumValue("Range", "Reach range", 4.0, 3.0, 6.0, 0.1);

   public Reach() {
      super("Reach", "Longer hit distance", Category.Ghost);
      this.addValues(new Value[]{this.range, this.interact});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(String.valueOf(this.range.getValue()));
   }
}
