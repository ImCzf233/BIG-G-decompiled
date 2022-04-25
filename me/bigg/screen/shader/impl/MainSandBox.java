package me.bigg.screen.shader.impl;

import me.bigg.screen.shader.GLSLShader;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class MainSandBox extends GLSLShader {
   private long lastMS = System.currentTimeMillis();
   private float time;

   public MainSandBox() {
      super("sandbox.frag");
   }

   public void setupUniforms() {
      this.setupUniform("resolution");
      this.setupUniform("time");
   }

   public void updateUniforms() {
      long currentMS = System.currentTimeMillis();
      long delta = currentMS - this.lastMS;
      this.lastMS = currentMS;
      ScaledResolution scaledResolution = new ScaledResolution(mc);
      int resolutionID = this.getUniform("resolution");
      if (resolutionID > -1) {
         GL20.glUniform2f(resolutionID, (float)scaledResolution.getScaledWidth() * 2.0F, (float)scaledResolution.getScaledHeight() * 2.0F);
      }

      int timeID = this.getUniform("time");
      if (timeID > -1) {
         GL20.glUniform1f(timeID, this.time);
      }

      this.time += 0.002F * (float)delta;
   }
}
