package me.bigg.module.impl.movement.phase;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.movement.phase.impl.Skip;
import me.bigg.module.impl.movement.phase.impl.Vanilla;
import me.bigg.value.EnumValue;
import me.bigg.value.Value;

public class Phase extends Module {
   private final EnumValue modeValue = new EnumValue("Mode", "fuck it", Phase.modeEnums.values());
   private final Vanilla vanilla = new Vanilla();
   private final Skip skip = new Skip();
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$movement$phase$Phase$modeEnums;

   public Phase() {
      super("Phase", "Get out of the blocks", Category.Movement);
      this.addValues(new Value[]{this.modeValue});
   }

   public void onDisable() {
      super.onDisable();
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name());
      switch ($SWITCH_TABLE$me$bigg$module$impl$movement$phase$Phase$modeEnums()[((modeEnums)this.modeValue.getMode()).ordinal()]) {
         case 1:
            this.vanilla.onUpdate(event);
            break;
         case 2:
            this.skip.onUpdate(event);
      }

   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$movement$phase$Phase$modeEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$movement$phase$Phase$modeEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Phase.modeEnums.values().length];

         try {
            var0[Phase.modeEnums.NCP.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Phase.modeEnums.Skip.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Phase.modeEnums.Vanilla.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$movement$phase$Phase$modeEnums = var0;
         return var0;
      }
   }

   private static enum modeEnums {
      Vanilla,
      Skip,
      NCP;
   }
}
