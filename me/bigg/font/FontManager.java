package me.bigg.font;

import java.awt.Font;
import java.io.InputStream;
import me.bigg.font.cfont.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontManager {
   public CFontRenderer arial12 = new CFontRenderer(getFont("annex/Arial.ttf", 12), true, true);
   public CFontRenderer arial14 = new CFontRenderer(getFont("annex/Arial.ttf", 14), true, true);
   public CFontRenderer arial16 = new CFontRenderer(getFont("annex/Arial.ttf", 16), true, true);
   public CFontRenderer arial18 = new CFontRenderer(getFont("annex/Arial.ttf", 18), true, true);
   public CFontRenderer arial20 = new CFontRenderer(getFont("annex/Arial.ttf", 20), true, true);
   public CFontRenderer arial22 = new CFontRenderer(getFont("annex/Arial.ttf", 22), true, true);
   public CFontRenderer arial24 = new CFontRenderer(getFont("annex/Arial.ttf", 24), true, true);
   public CFontRenderer arial26 = new CFontRenderer(getFont("annex/Arial.ttf", 26), true, true);
   public CFontRenderer arial28 = new CFontRenderer(getFont("annex/Arial.ttf", 28), true, true);
   public CFontRenderer arial32 = new CFontRenderer(getFont("annex/Arial.ttf", 32), true, true);
   public CFontRenderer arial40 = new CFontRenderer(getFont("annex/Arial.ttf", 40), true, true);
   public CFontRenderer arial42 = new CFontRenderer(getFont("annex/Arial.ttf", 42), true, true);
   public CFontRenderer logo32 = new CFontRenderer(getFont("annex/logo.ttf", 32), true, true);
   public static CFontRenderer splash40 = new CFontRenderer(getFont("annex/Arial.ttf", 40), true, true);
   public static CFontRenderer splash18 = new CFontRenderer(getFont("annex/Arial.ttf", 18), true, true);

   private static Font getFont(String location, int size) {
      Font font;
      try {
         InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(location)).getInputStream();
         font = Font.createFont(0, is);
         font = font.deriveFont(0, (float)size);
      } catch (Exception var4) {
         var4.printStackTrace();
         System.out.println("Error loading font");
         font = new Font("default", 0, size);
      }

      return font;
   }
}
