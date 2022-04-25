package me.bigg.screen.click2;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import me.bigg.Client;
import me.bigg.module.Category;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class ClickyUI extends GuiScreen {
   public static ArrayList windows = Lists.newArrayList();
   public double opacity = 0.0;
   public int scrollVelocity;
   public static boolean binding = false;

   public ClickyUI() {
      if (windows.isEmpty()) {
         int x = 5;
         Category[] arrmoduleType = Category.values();
         int n = arrmoduleType.length;

         for(int n2 = 0; n2 < n; ++n2) {
            Category c = arrmoduleType[n2];
            windows.add(new Window(c, x, 5));
            x += 95;
         }
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      double d = this.opacity + 10.0 < 200.0 ? (this.opacity += 10.0) : 200.0;
      this.opacity = d;
      new Color(-2146365167);
      GlStateManager.pushMatrix();
      ScaledResolution scaledRes = new ScaledResolution(this.mc);
      float scale = (float)scaledRes.getScaleFactor() / (float)Math.pow((double)scaledRes.getScaleFactor(), 5.0);
      windows.forEach((w) -> {
         w.render(mouseX, mouseY);
      });
      GlStateManager.popMatrix();
      if (Mouse.hasWheel()) {
         int wheel = Mouse.getDWheel();
         this.scrollVelocity = wheel < 0 ? -120 : (wheel > 0 ? 130 : 0);
      }

      windows.forEach((w) -> {
         w.mouseScroll(mouseX, mouseY, this.scrollVelocity);
      });
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      windows.forEach((w) -> {
         w.click(mouseX, mouseY, mouseButton);
      });
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1 && !binding) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else {
         windows.forEach((w) -> {
            w.key(typedChar, keyCode);
         });
      }
   }

   public void onGuiClosed() {
      Client.INSTANCE.shutDown();
      super.onGuiClosed();
   }

   public synchronized void sendToFront(Window window) {
      int panelIndex = 0;

      for(int i = 0; i < windows.size(); ++i) {
         if (windows.get(i) == window) {
            panelIndex = i;
            break;
         }
      }

      Window t = (Window)windows.get(windows.size() - 1);
      windows.set(windows.size() - 1, (Window)windows.get(panelIndex));
      windows.set(panelIndex, t);
   }
}
