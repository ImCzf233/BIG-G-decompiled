package me.bigg.util;

import java.awt.Color;
import me.bigg.module.Category;
import org.lwjgl.opengl.GL11;

public class ColorUtil {
   public static Color reAlpha(Color before, int alpha) {
      return new Color(before.getRed(), before.getGreen(), before.getBlue(), alpha);
   }

   public static Color getRainbow(float second, float sat, float bright) {
      float hue = (float)(System.currentTimeMillis() % (long)((int)(second * 1000.0F))) / (second * 1000.0F);
      return new Color(Color.HSBtoRGB(hue, sat, bright));
   }

   public static Color getRainbow(float second, float sat, float bright, long index) {
      float hue = (float)((System.currentTimeMillis() + index) % (long)((int)(second * 1000.0F))) / (second * 1000.0F);
      return new Color(Color.HSBtoRGB(hue, sat, bright));
   }

   public static Color getFadeRainbow(Color color, int index, int count) {
      float[] hsb = new float[3];
      Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
      float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + (float)index / (float)count * 2.0F) % 2.0F - 1.0F);
      brightness = 0.5F + 0.5F * brightness;
      hsb[2] = brightness % 2.0F;
      return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
   }

   public static Color getBlendColor(double current, double max) {
      long base = Math.round(max / 5.0);
      if (current >= (double)(base * 5L)) {
         return new Color(15, 255, 15);
      } else if (current >= (double)(base * 4L)) {
         return new Color(166, 255, 0);
      } else if (current >= (double)(base * 3L)) {
         return new Color(255, 191, 0);
      } else {
         return current >= (double)(base * 2L) ? new Color(255, 89, 0) : new Color(255, 0, 0);
      }
   }

   public static int getCategoryColor(Category category) {
      int color = -1;
      if (category == Category.Combat) {
         color = (new Color(150, 53, 46)).getRGB();
      } else if (category == Category.Movement) {
         color = (new Color(200, 121, 0)).getRGB();
      } else if (category == Category.Player) {
         color = (new Color(65, 134, 45)).getRGB();
      } else if (category == Category.Vision) {
         color = (new Color(65, 118, 146)).getRGB();
      } else if (category == Category.Misc) {
         color = (new Color(65, 155, 103)).getRGB();
      } else if (category == Category.Ghost) {
         color = (new Color(66, 111, 214)).getRGB();
      }

      return color;
   }

   public static Color getDarker(Color before, int dark, int alpha) {
      int rDank = Math.max(before.getRed() - dark, 0);
      int gDank = Math.max(before.getGreen() - dark, 0);
      int bDank = Math.max(before.getBlue() - dark, 0);
      return new Color(rDank, gDank, bDank, alpha);
   }

   public static Color getLighter(Color before, int light, int alpha) {
      int rDank = Math.min(before.getRed() + light, 255);
      int gDank = Math.min(before.getGreen() + light, 255);
      int bDank = Math.min(before.getBlue() + light, 255);
      return new Color(rDank, gDank, bDank, alpha);
   }

   public static Color getRandomColor() {
      return new Color(MathUtil.getRandom().nextInt(256), MathUtil.getRandom().nextInt(256), MathUtil.getRandom().nextInt(256));
   }

   public static void glColor(int color) {
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GL11.glColor4f(f1, f2, f3, f);
   }
}
