package me.bigg.util;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import me.bigg.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class CompassUtil {
   public float innerWidth;
   public float outerWidth;
   public boolean shadow;
   public float scale;
   public int accuracy;
   public static List degrees = Lists.newArrayList();

   public CompassUtil(float i, float o, float s, int a, boolean sh) {
      this.innerWidth = i;
      this.outerWidth = o;
      this.scale = s;
      this.accuracy = a;
      this.shadow = sh;
      degrees.add(new Degree("N", 1));
      degrees.add(new Degree("195", 2));
      degrees.add(new Degree("210", 2));
      degrees.add(new Degree("NE", 3));
      degrees.add(new Degree("240", 2));
      degrees.add(new Degree("255", 2));
      degrees.add(new Degree("E", 1));
      degrees.add(new Degree("285", 2));
      degrees.add(new Degree("300", 2));
      degrees.add(new Degree("SE", 3));
      degrees.add(new Degree("330", 2));
      degrees.add(new Degree("345", 2));
      degrees.add(new Degree("S", 1));
      degrees.add(new Degree("15", 2));
      degrees.add(new Degree("30", 2));
      degrees.add(new Degree("SW", 3));
      degrees.add(new Degree("60", 2));
      degrees.add(new Degree("75", 2));
      degrees.add(new Degree("W", 1));
      degrees.add(new Degree("105", 2));
      degrees.add(new Degree("120", 2));
      degrees.add(new Degree("NW", 3));
      degrees.add(new Degree("150", 2));
      degrees.add(new Degree("165", 2));
   }

   public void draw(ScaledResolution sr) {
      preRender(sr);
      float center = (float)sr.getScaledWidth() / 2.0F;
      int count = 0;
      int cardinals = 0;
      int subCardinals = 0;
      int markers = 0;
      float offset = 0.0F;
      float yaaahhrewindTime = Minecraft.getMinecraft().thePlayer.rotationYaw % 360.0F * 2.0F + 1080.0F;
      RenderUtil.startGlScissor(sr.getScaledWidth() / 2 - 100, 25, 200, 25);

      Degree d;
      Iterator var10;
      float location;
      float completeLocation;
      for(var10 = degrees.iterator(); var10.hasNext(); ++count) {
         d = (Degree)var10.next();
         location = center + (float)(count * 30) - yaaahhrewindTime;
         completeLocation = d.type == 1 ? location - (float)(Client.INSTANCE.getFontManager().arial28.getStringWidth(d.text) / 2) : (d.type == 2 ? location - (float)(Client.INSTANCE.getFontManager().arial28.getStringWidth(d.text) / 2) : location - (float)(Client.INSTANCE.getFontManager().arial22.getStringWidth(d.text) / 2));
         int opacity = opacity(sr, completeLocation);
         if (d.type == 1 && opacity != 16777215) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial28.drawStringWithShadow(d.text, (double)completeLocation, 25.0, opacity(sr, completeLocation));
            ++cardinals;
         }

         if (d.type == 2 && opacity != 16777215) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtil.drawRect((double)(location - 0.5F), 29.0, (double)(location + 0.5F), 34.0, opacity(sr, completeLocation));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial14.drawString(d.text, completeLocation, 37.5F, opacity(sr, completeLocation));
            ++markers;
         }

         if (d.type == 3 && opacity != 16777215) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial22.drawStringWithShadow(d.text, (double)completeLocation, (double)(25.0F + (float)Client.INSTANCE.getFontManager().arial28.getHeight() / 2.0F - (float)Client.INSTANCE.getFontManager().arial22.getHeight() / 2.0F), opacity(sr, completeLocation));
            ++subCardinals;
         }
      }

      for(var10 = degrees.iterator(); var10.hasNext(); ++count) {
         d = (Degree)var10.next();
         location = center + (float)(count * 30) - yaaahhrewindTime;
         completeLocation = d.type == 1 ? location - (float)(Client.INSTANCE.getFontManager().arial28.getStringWidth(d.text) / 2) : (d.type == 2 ? location - (float)(Client.INSTANCE.getFontManager().arial14.getStringWidth(d.text) / 2) : location - (float)(Client.INSTANCE.getFontManager().arial22.getStringWidth(d.text) / 2));
         if (d.type == 1) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial28.drawStringWithShadow(d.text, (double)completeLocation, 25.0, opacity(sr, completeLocation));
            ++cardinals;
         }

         if (d.type == 2) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtil.drawRect((double)(location - 0.5F), 29.0, (double)(location + 0.5F), 34.0, opacity(sr, completeLocation));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial14.drawString(d.text, completeLocation, 37.5F, opacity(sr, completeLocation));
            ++markers;
         }

         if (d.type == 3) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial22.drawStringWithShadow(d.text, (double)completeLocation, (double)(25.0F + (float)Client.INSTANCE.getFontManager().arial28.getHeight() / 2.0F - (float)(Client.INSTANCE.getFontManager().arial22.getHeight() / 2)), opacity(sr, completeLocation));
            ++subCardinals;
         }
      }

      for(var10 = degrees.iterator(); var10.hasNext(); ++count) {
         d = (Degree)var10.next();
         location = center + (float)(count * 30) - yaaahhrewindTime;
         completeLocation = d.type == 1 ? location - (float)(Client.INSTANCE.getFontManager().arial28.getStringWidth(d.text) / 2) : (d.type == 2 ? location - (float)(Client.INSTANCE.getFontManager().arial14.getStringWidth(d.text) / 2) : location - (float)(Client.INSTANCE.getFontManager().arial22.getStringWidth(d.text) / 2));
         if (d.type == 1) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial28.drawStringWithShadow(d.text, (double)completeLocation, 25.0, opacity(sr, completeLocation));
            ++cardinals;
         }

         if (d.type == 2) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtil.drawRect((double)(location - 0.5F), 29.0, (double)(location + 0.5F), 34.0, opacity(sr, completeLocation));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial14.drawString(d.text, completeLocation, 37.5F, opacity(sr, completeLocation));
            ++markers;
         }

         if (d.type == 3) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Client.INSTANCE.getFontManager().arial22.drawStringWithShadow(d.text, (double)completeLocation, (double)(25.0F + (float)Client.INSTANCE.getFontManager().arial28.getHeight() / 2.0F - (float)Client.INSTANCE.getFontManager().arial22.getHeight() / 2.0F), opacity(sr, completeLocation));
            ++subCardinals;
         }
      }

      RenderUtil.stopGlScissor();
   }

   public static void preRender(ScaledResolution sr) {
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
   }

   public static int opacity(ScaledResolution sr, float offset) {
      int op = false;
      float offs = 255.0F - Math.abs((float)sr.getScaledWidth() / 2.0F - offset) * 1.8F;
      Color c = new Color(255, 255, 255, (int)Math.min(Math.max(0.0F, offs), 255.0F));
      return c.getRGB();
   }

   public class Degree {
      public String text;
      public int type;

      public Degree(String s, int t) {
         this.text = s;
         this.type = t;
      }
   }
}
