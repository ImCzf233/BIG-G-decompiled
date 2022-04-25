package me.bigg.util.sub.particle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import me.bigg.util.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class ParticleRenderUtils {
   public static float delta;

   public static String getShaderCode(InputStreamReader file) {
      String shaderSource = "";

      try {
         String line;
         BufferedReader reader;
         for(reader = new BufferedReader(file); (line = reader.readLine()) != null; shaderSource = String.valueOf(shaderSource) + line + "\n") {
         }

         reader.close();
      } catch (IOException var4) {
         var4.printStackTrace();
         System.exit(-1);
      }

      return shaderSource.toString();
   }

   public static int createShader(String shaderCode, int shaderType) throws Exception {
      int shader = 0;

      try {
         shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
         if (shader == 0) {
            return 0;
         }
      } catch (Exception var4) {
         ARBShaderObjects.glDeleteObjectARB(shader);
         throw var4;
      }

      ARBShaderObjects.glShaderSourceARB(shader, (CharSequence)shaderCode);
      ARBShaderObjects.glCompileShaderARB(shader);
      if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
         throw new RuntimeException("Error creating shader:");
      } else {
         return shader;
      }
   }

   public static void connectPoints(float xOne, float yOne, float xTwo, float yTwo, int color) {
      GL11.glPushMatrix();
      GL11.glEnable(2848);
      ColorUtil.glColor(color);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glLineWidth(0.5F);
      GL11.glBegin(1);
      GL11.glVertex2f(xOne, yOne);
      GL11.glVertex2f(xTwo, yTwo);
      GL11.glEnd();
      GlStateManager.resetColor();
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawCircle(float x, float y, float radius, int color) {
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      GL11.glColor4f(red, green, blue, alpha);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GL11.glPushMatrix();
      GL11.glLineWidth(1.0F);
      GL11.glBegin(9);

      for(int i = 0; i <= 360; ++i) {
         GL11.glVertex2d((double)x + Math.sin((double)i * Math.PI / 180.0) * (double)radius, (double)y + Math.cos((double)i * Math.PI / 180.0) * (double)radius);
      }

      GL11.glEnd();
      GL11.glPopMatrix();
      GL11.glEnable(3553);
      GL11.glDisable(2848);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
