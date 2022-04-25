package me.bigg.module.impl.vision;

import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;

public class OldHitting extends Module {
   public final NumValue blockHValue = new NumValue("Block H", "Block H", 0.0, -1.5, 1.5, 0.05);
   public final NumValue blockVValue = new NumValue("Block V", "Block V", 0.0, -1.5, 1.5, 0.05);
   public final NumValue blockSValue = new NumValue("Block S", "Block S", 0.0, -1.5, 1.5, 0.05);
   public final NumValue armHValue = new NumValue("Arm H", "Arm H", 0.0, -3.0, 3.0, 0.05);
   public final NumValue armVValue = new NumValue("Arm V", "Arm V", 0.0, -3.0, 3.0, 0.05);
   public final NumValue armSValue = new NumValue("Arm S", "Arm S", 0.0, -3.0, 3.0, 0.05);
   public final NumValue swingSlowValue = new NumValue("Swing", "Novoline moments", 0.0, -3.0, 15.0, 1);
   public final BoolValue forceHeightValue = new BoolValue("Force Height", "Just force", false);
   public final EnumValue modeValue = new EnumValue("Mode", "Block mode", OldHitting.modeEnums.values());

   public OldHitting() {
      super("Old Hitting", "1.7 hitting", Category.Vision);
      this.addValues(new Value[]{this.modeValue, this.forceHeightValue, this.blockHValue, this.blockVValue, this.blockSValue, this.armHValue, this.armVValue, this.armSValue, this.swingSlowValue});
   }

   public static enum modeEnums {
      None,
      Old,
      Push,
      Slide,
      Jello,
      Rainy,
      Swang,
      Swong,
      Swong2,
      Dislike,
      Leaked;
   }
}
