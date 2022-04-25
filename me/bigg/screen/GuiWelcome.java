package me.bigg.screen;

import java.awt.Color;
import me.bigg.Client;
import me.bigg.util.TimerUtil;
import net.minecraft.client.gui.GuiScreen;

public class GuiWelcome extends GuiScreen {
   private int alpha = 0;
   private int retryTimes = -1;
   private final TimerUtil retryTimer = new TimerUtil();
   private final String[] messages = new String[]{"", ""};

   public GuiWelcome() {
      this.retryTimer.reset();
      this.messages[0] = "Checking your network...";
      this.messages[1] = "Check your network connection to continue";
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      drawRect(0, 0, this.width, this.height, Color.BLACK.getRGB());
      if (Client.INSTANCE.isConnected()) {
         this.alpha = Math.max(this.alpha - 7, 0);
      } else {
         this.alpha = Math.min(this.alpha + 7, 255);
      }

      int color = (new Color(255, 255, 255, this.alpha)).getRGB();
      String var10001 = this.messages[0];
      float var10002 = (float)this.width / 2.0F;
      Client.INSTANCE.getFontManager().arial40.drawCenteredString(var10001, var10002, (float)this.height / 2.0F - 15.0F, color);
      var10001 = this.messages[1];
      var10002 = (float)this.width / 2.0F;
      Client.INSTANCE.getFontManager().arial18.drawCenteredString(var10001, var10002, (float)this.height / 2.0F + 8.0F, color);
      if (this.retryTimes < 0) {
         this.retryTimer.reset();
      }

      if (this.retryTimes != 999) {
         if (this.alpha >= 255 && (this.retryTimer.hasPassed(1000L) || this.retryTimes == -1)) {
            this.messages[0] = "Connected to the server";
            this.messages[1] = "You can login now";
            this.retryTimes = 999;
            (new Thread(() -> {
               try {
                  Thread.sleep(1500L);
               } catch (InterruptedException var3) {
                  var3.printStackTrace();
               }

               Client.INSTANCE.setConnected(true);

               try {
                  Thread.sleep(1500L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
               }

               this.mc.displayGuiScreen(new GuiClientMainMenu());
            })).start();
         }
      }
   }

   private boolean canRetry() {
      return this.retryTimes < 3;
   }
}
