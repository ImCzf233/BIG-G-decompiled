package me.bigg.util;

import me.bigg.util.sub.particle.ParticleGenerator;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ParticleUtil {
   private static final ParticleGenerator particleGenerator = new ParticleGenerator(100);

   public static void drawParticles(int mouseX, int mouseY, int color) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GL11.glEnable(2881);
      particleGenerator.draw(mouseX, mouseY, color);
      GL11.glDisable(2881);
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }
}
