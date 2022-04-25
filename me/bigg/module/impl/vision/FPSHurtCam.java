package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import me.bigg.event.OverlayEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

public class FPSHurtCam extends Module {
   public FPSHurtCam() {
      super("FPS Hurt Cam", "Hurt cam in fps games", Category.Vision);
   }

   @EventTarget(0)
   void onOverlay(OverlayEvent event) {
      ScaledResolution sr = event.getScaledResolution();
      RenderUtil.drawVerticalGradientSideways(0.0, 0.0, (double)sr.getScaledWidth(), 30.0, (new Color(255, 15, 15, mc.thePlayer.hurtTime * 20)).getRGB(), 0);
      RenderUtil.drawVerticalGradientSideways(0.0, (double)(sr.getScaledHeight() - 30), (double)sr.getScaledWidth(), (double)sr.getScaledHeight(), 0, (new Color(255, 15, 15, mc.thePlayer.hurtTime * 20)).getRGB());
      RenderUtil.drawHorizontalGradientSideways(0.0, 0.0, 30.0, (double)sr.getScaledHeight(), (new Color(255, 15, 15, mc.thePlayer.hurtTime * 20)).getRGB(), 0);
      RenderUtil.drawHorizontalGradientSideways((double)(sr.getScaledWidth() - 30), 0.0, (double)sr.getScaledWidth(), (double)sr.getScaledHeight(), 0, (new Color(255, 15, 15, mc.thePlayer.hurtTime * 20)).getRGB());
   }
}
