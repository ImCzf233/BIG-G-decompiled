package me.bigg.module.impl.ghost;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;
import org.lwjgl.input.Keyboard;

public class Eagle extends Module {
   public Eagle() {
      super("Eagle", "Shift when no a block under you", Category.Ghost);
   }

   public void onDisable() {
      super.onDisable();
      mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (mc.thePlayer.capabilities.isFlying) {
         mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
      } else {
         mc.gameSettings.keyBindSneak.pressed = PlayerUtil.isAirUnder(mc.thePlayer);
      }
   }
}
