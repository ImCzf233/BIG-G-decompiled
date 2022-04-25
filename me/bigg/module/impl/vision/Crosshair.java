package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import me.bigg.event.CrosshairEvent;
import me.bigg.event.OverlayEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.PlayerUtil;
import me.bigg.util.RenderUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.gui.ScaledResolution;

public class Crosshair extends Module {
   private final BoolValue dotValue = new BoolValue("Dot", "Middle dot", false);
   private final BoolValue TValue = new BoolValue("T Scale", "T be like", false);
   private final NumValue lengthValue = new NumValue("Length", "Cross hair length", 5.5, 0.1, 10.0, 0.1);
   private final NumValue thickNessValue = new NumValue("Thickness", "Cross hair length", 0.1, 0.1, 10.0, 0.1);
   private final NumValue gapValue = new NumValue("Gap", "Cross hair gap", 2.8, 0.1, 10.0, 0.1);
   private final NumValue redValue = new NumValue("Red", "Red color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue greenValue = new NumValue("Green", "Green color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue blueValue = new NumValue("Blue", "Blue color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue alphaValue = new NumValue("Alpha", "Cross hair Alpha", 255.0, 0, 255.0, 5.0);

   public Crosshair() {
      super("Cross hair", "sb", Category.Vision);
      this.addValues(new Value[]{this.TValue, this.dotValue, this.lengthValue, this.thickNessValue, this.gapValue, this.redValue, this.greenValue, this.blueValue, this.alphaValue});
   }

   @EventTarget
   void onRenderCross(CrosshairEvent event) {
      event.setCancelled(true);
   }

   @EventTarget
   void onOverlay(OverlayEvent event) {
      ScaledResolution sr = event.getScaledResolution();
      int width = sr.getScaledWidth();
      int height = sr.getScaledHeight();
      int color = (new Color(((Number)this.redValue.getValue()).intValue(), ((Number)this.greenValue.getValue()).intValue(), ((Number)this.blueValue.getValue()).intValue(), ((Number)this.alphaValue.getValue()).intValue())).getRGB();
      double length = ((Number)this.lengthValue.getValue()).doubleValue();
      double thick = ((Number)this.thickNessValue.getValue()).doubleValue();
      double gap = ((Number)this.gapValue.getValue()).doubleValue();
      double forLength = 0.5 + length;
      if (PlayerUtil.MovementInput()) {
         ++gap;
      }

      if (!mc.thePlayer.onGround) {
         ++gap;
      }

      if ((Boolean)this.dotValue.getValue()) {
         RenderUtil.drawRect((double)width / 2.0 - 0.5, (double)height / 2.0 - 0.5, (double)width / 2.0 + 0.5, (double)height / 2.0 + 0.5, color);
      }

      RenderUtil.drawRect((double)width / 2.0 - 0.5 - length - forLength - gap, (double)height / 2.0 - 0.5 - thick, (double)width / 2.0 - 0.5 - gap, (double)height / 2.0 + 0.5 + thick, color);
      RenderUtil.drawRect((double)width / 2.0 + 0.5 + gap, (double)height / 2.0 - 0.5 - thick, (double)width / 2.0 + 0.5 + length + forLength + gap, (double)height / 2.0 + 0.5 + thick, color);
      if (!(Boolean)this.TValue.getValue()) {
         RenderUtil.drawRect((double)width / 2.0 - 0.5 - thick, (double)height / 2.0 - 0.5 - length - forLength - gap, (double)width / 2.0 + 0.5 + thick, (double)height / 2.0 - 0.5 - gap, color);
      }

      RenderUtil.drawRect((double)width / 2.0 - 0.5 - thick, (double)height / 2.0 + 0.5 + gap, (double)width / 2.0 + 0.5 + thick, (double)height / 2.0 + 0.5 + length + forLength + gap, color);
   }
}
