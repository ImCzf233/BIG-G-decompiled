package me.bigg.screen;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.bigg.Client;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.screen.login.GuiAltLogin;
import me.bigg.util.MathUtil;
import me.bigg.util.ParticleUtil;
import me.bigg.util.RenderUtil;
import me.bigg.util.TranslateUtil;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiClientMainMenu extends GuiScreen {
   private int alpha;
   private boolean inChangeLog;
   private TranslateUtil translate = new TranslateUtil(0.0F, 0.0F);
   private static String verChecks = "§cChecking...";
   private float translateX;
   private float translateY;

   public GuiClientMainMenu() {
      this.alpha = Client.INSTANCE.isAnimated() ? 0 : 255;
      this.inChangeLog = false;
      this.translate.setY(0.0F);
      this.translate.setX(0.0F);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.drawDefaultBackground();
      ScaledResolution sr = new ScaledResolution(this.mc);
      GlStateManager.pushMatrix();
      this.translateX += ((float)mouseX - (float)this.width / 2.0F - this.translateX) / (float)sr.getScaleFactor() * 0.5F;
      this.translateY += ((float)mouseY - (float)this.height / 2.0F - this.translateY) / (float)sr.getScaleFactor() * 0.5F;
      GlStateManager.translate(this.translateX / 50.0F, this.translateY / 50.0F, 0.0F);
      ParticleUtil.drawParticles(mouseX, mouseY, -1);
      CFontRenderer font = Client.INSTANCE.getFontManager().arial18;
      CFontRenderer small = Client.INSTANCE.getFontManager().arial14;
      int trans = 0;

      int logY;
      for(logY = 0; logY < 5; ++logY) {
         boolean inIt = !this.inChangeLog && MathUtil.inRange((double)mouseX, (double)mouseY, (double)((float)this.width / 2.0F + 70.0F), (double)((float)this.height / 2.0F - 21.5F + (float)trans), (double)((float)this.width / 2.0F - 70.0F), (double)((float)this.height / 2.0F - 37.5F + (float)trans));
         RenderUtil.drawRect((double)((float)this.width / 2.0F - 70.0F), (double)((float)this.height / 2.0F - 37.5F + (float)trans), (double)((float)this.width / 2.0F + 70.0F), (double)((float)this.height / 2.0F - 21.5F + (float)trans), (new Color(0, 0, 0, inIt ? 200 : 150)).getRGB());
         switch (logY) {
            case 0:
               font.drawCenteredString("Single Player", (float)this.width / 2.0F, (float)this.height / 2.0F - 37.0F + 5.0F + (float)trans, -1);
               break;
            case 1:
               font.drawCenteredString("Multi Player", (float)this.width / 2.0F, (float)this.height / 2.0F - 37.0F + 5.0F + (float)trans, -1);
               break;
            case 2:
               font.drawCenteredString("Alt Manager", (float)this.width / 2.0F, (float)this.height / 2.0F - 37.0F + 5.0F + (float)trans, -1);
               break;
            case 3:
               font.drawCenteredString("Options", (float)this.width / 2.0F, (float)this.height / 2.0F - 37.0F + 5.0F + (float)trans, -1);
               break;
            case 4:
               font.drawCenteredString("Logs", (float)this.width / 2.0F, (float)this.height / 2.0F - 37.0F + 5.0F + (float)trans, -1);
         }

         trans += 18;
      }

      font.drawStringWithShadow("Welcome back ! §7[" + Client.USERNAME + "§7]§f " + Client.USERNAME + " (" + "1" + ") , Your are on b" + Client.INSTANCE.getBuild() + " §7[" + verChecks + "§7]", 4.0, (double)(this.height - 10), (new Color(255, 255, 255, 200)).getRGB());
      GlStateManager.popMatrix();
      RenderUtil.drawRect(0.0, 0.0, (double)this.width, (double)this.height, (new Color(0, 0, 0, (int)this.translate.getX())).getRGB());
      logY = 0;
      List strList = Arrays.asList("Added grille value to click gui", "Improved Click Gui", "Added Border value to click gui", "Added Swing mode to aura", "Improved Net Work Assist", "Fixed array list bugs", "Added Tab Gui", "Improved HUD", "Added lock view to aura", "Improved performance");
      strList.sort((o1, o2) -> {
         return small.getStringWidth(o2) - small.getStringWidth(o1);
      });
      font.drawStringWithShadow("§7b" + Client.INSTANCE.getBuild() + " Change Logs :", 5.0, 5.0, (new Color(255, 255, 255, (int)this.translate.getY())).getRGB());
      font.drawStringWithShadow("Click anywhere to exit", (double)(this.width - font.getStringWidth("Click anywhere to exit") - 5), 5.0, (new Color(255, 255, 255, (int)this.translate.getY())).getRGB());

      for(Iterator var11 = strList.iterator(); var11.hasNext(); logY += 10) {
         String str = (String)var11.next();
         small.drawStringWithShadow(str, 5.0, (double)(18 + logY), (new Color(255, 255, 255, (int)this.translate.getY())).getRGB());
      }

      this.translate.interpolate((float)(this.inChangeLog ? 200 : 0), (float)(this.inChangeLog ? 255 : 0), 0.1F);
      this.alpha = Math.max(this.alpha -= 5, 0);
      drawRect(0, 0, this.width, this.height, (new Color(0, 0, 0, this.alpha)).getRGB());
      if (!Client.INSTANCE.isAnimated()) {
         Client.INSTANCE.setAnimated(true);
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 0) {
         if (!this.inChangeLog) {
            int trans = 0;

            for(int i = 0; i < 5; ++i) {
               boolean inIt = MathUtil.inRange((double)mouseX, (double)mouseY, (double)((float)this.width / 2.0F + 70.0F), (double)((float)this.height / 2.0F - 21.5F + (float)trans), (double)((float)this.width / 2.0F - 70.0F), (double)((float)this.height / 2.0F - 37.5F + (float)trans));
               if (i == 0 && inIt) {
                  this.mc.displayGuiScreen(new GuiSelectWorld(this));
                  break;
               }

               if (i == 1 && inIt) {
                  this.mc.displayGuiScreen(new GuiMultiplayer(this));
                  break;
               }

               if (i == 2 && inIt) {
                  this.mc.displayGuiScreen(new GuiAltLogin(this));
                  break;
               }

               if (i == 3 && inIt) {
                  this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                  break;
               }

               if (i == 4 && inIt) {
                  this.inChangeLog = true;
                  break;
               }

               trans += 18;
            }
         } else {
            this.inChangeLog = false;
         }
      }

   }

   public static void setVerChecks(String ver) {
      verChecks = ver;
   }
}
