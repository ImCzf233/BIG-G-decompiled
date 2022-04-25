package me.bigg.screen;

import java.awt.Color;
import me.bigg.Client;
import net.minecraft.client.gui.GuiScreen;

public class GuiCausedError extends GuiScreen {
   private int alpha = 0;
   private final long exitTime;
   private final String big;
   private final String small;
   private boolean active = false;

   public GuiCausedError(String big, String small, long exitTime) {
      this.big = big;
      this.small = small;
      this.exitTime = exitTime;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.alpha = Math.min(this.alpha + 7, 255);
      drawRect(0, 0, this.width, this.height, (new Color(0, 0, 0, this.alpha)).getRGB());
      float var10002 = (float)this.width / 2.0F;
      Client.INSTANCE.getFontManager().arial40.drawCenteredString(this.big, var10002, (float)this.height / 2.0F - 15.0F, (new Color(255, 255, 255, this.alpha)).getRGB());
      var10002 = (float)this.width / 2.0F;
      Client.INSTANCE.getFontManager().arial18.drawCenteredString(this.small, var10002, (float)this.height / 2.0F + 8.0F, (new Color(255, 255, 255, this.alpha)).getRGB());
      if (this.alpha >= 255 && !this.active) {
         (new Thread(() -> {
            try {
               Thread.sleep(this.exitTime);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }

            this.mc.shutdown();
         })).start();
         this.active = true;
      }

   }
}
