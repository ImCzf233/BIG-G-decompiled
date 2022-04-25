package me.bigg.module.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.MathUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.Value;
import org.lwjgl.input.Keyboard;

public class Clown extends Module {
   private final BoolValue dance = new BoolValue("Dance", "Jump and sneak", true);

   public Clown() {
      super("Clown", "Rotate like a clown", Category.Misc);
      this.addValues(new Value[]{this.dance});
   }

   public void onDisable() {
      super.onDisable();
      if ((Boolean)this.dance.getValue()) {
         mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
         mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
      }
   }

   @EventTarget(4)
   void onUpdate(UpdateEvent event) {
      event.setYaw((float)MathUtil.getRandom(-180, 180));
      event.setPitch((float)MathUtil.getRandom(-90, 90));
      if ((Boolean)this.dance.getValue()) {
         mc.gameSettings.keyBindJump.pressed = MathUtil.getRandom().nextBoolean();
         mc.gameSettings.keyBindSneak.pressed = MathUtil.getRandom().nextBoolean();
      }
   }
}
