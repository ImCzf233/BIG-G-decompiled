package me.bigg.module.impl.vision;

import me.bigg.Client;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;

public class CGui extends Module {
   private static final EnumValue grilleValue = new EnumValue("Grille", "Exhi has this", CGui.grilleEnums.values());
   private static final BoolValue bottomValue = new BoolValue("Bottom", "If u don't know , toggle it and look", true);
   private static final BoolValue borderValue = new BoolValue("Border", "Just bord the window", false);
   private static final NumValue gradientValue = new NumValue("Bottom gradient", "Depends on ur PC performers", 8.0, 1.0, 30.0, 1.0);
   private final EnumValue modeValue = new EnumValue("Mode", "modes", CGui.modeEnums.values());

   public CGui() {
      super("Click Gui", "Your click gui", Category.Vision);
      this.addValues(new Value[]{this.modeValue, grilleValue, bottomValue, borderValue, gradientValue});
      this.setKeybinding(54);
   }

   public void onEnable() {
      super.onEnable();
      this.toggle();
      if (this.modeValue.getValue() == CGui.modeEnums.New) {
         mc.displayGuiScreen(Client.INSTANCE.getgui1());
      } else {
         mc.displayGuiScreen(Client.INSTANCE.getgui2());
      }

   }

   public static EnumValue getGrilleValue() {
      return grilleValue;
   }

   public static BoolValue getBottomValue() {
      return bottomValue;
   }

   public static BoolValue getBorderValue() {
      return borderValue;
   }

   public static NumValue getGradientValue() {
      return gradientValue;
   }

   public static enum grilleEnums {
      None,
      HUD,
      Rainbow;
   }

   private static enum modeEnums {
      New,
      Old;
   }
}
