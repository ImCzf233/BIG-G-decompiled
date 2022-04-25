package me.bigg.screen.click2;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.ColorUtil;
import me.bigg.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class Window {
   public Category category;
   public ArrayList buttons = Lists.newArrayList();
   public boolean drag;
   public boolean extended;
   public int x;
   public int y;
   public int expand;
   public int dragX;
   public int dragY;
   public int max;
   public int scroll;
   public int scrollTo;
   public double angel;
   CFontRenderer font;

   public Window(Category category, int x, int y) {
      this.font = Client.INSTANCE.getFontManager().arial16;
      this.category = category;
      this.x = x;
      this.y = y;
      this.max = 120;
      int y2 = y + 22;
      Client.INSTANCE.getModuleManager();
      Iterator var6 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

      while(var6.hasNext()) {
         Module c = (Module)var6.next();
         if (c.getCategory() == category) {
            this.buttons.add(new Button(c, x + 5, y2));
            y2 += 15;
         }
      }

      var6 = this.buttons.iterator();

      while(var6.hasNext()) {
         Button b2 = (Button)var6.next();
         b2.setParent(this);
      }

   }

   public void render(int mouseX, int mouseY) {
      int current = 0;

      Iterator var7;
      for(Iterator var5 = this.buttons.iterator(); var5.hasNext(); current += 15) {
         Button b3 = (Button)var5.next();
         if (b3.expand) {
            for(var7 = b3.buttons.iterator(); var7.hasNext(); current += 15) {
               ValueButton v = (ValueButton)var7.next();
            }
         }
      }

      int height = 15 + current;
      int color;
      double d;
      if (this.extended) {
         color = this.expand + 5 < height ? (this.expand += 5) : height;
         this.expand = color;
         d = this.angel + 20.0 < 180.0 ? (this.angel += 20.0) : 180.0;
         this.angel = d;
      } else {
         color = this.expand - 5 > 0 ? (this.expand -= 5) : 0;
         this.expand = color;
         d = this.angel - 20.0 > 0.0 ? (this.angel -= 20.0) : 0.0;
         this.angel = d;
      }

      GlStateManager.pushMatrix();
      color = ColorUtil.getCategoryColor(this.category);
      Gui.drawRect(this.x - 1, this.y + 1, this.x + 91, this.y + 3 + this.expand, color);
      Gui.drawRect(this.x, this.y + 2, this.x + 90, this.y + 2 + this.expand, (new Color(46, 46, 46, 255)).getRGB());
      Gui.drawRect(this.x, this.y + 1, this.x + 90, this.y + 15, color);
      this.font.drawString(this.category.name(), (float)(this.x + 3), (float)(this.y + 6), (new Color(0, 0, 0)).getRGB());
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      if (this.expand > 0) {
         GlStateManager.pushMatrix();
         this.buttons.forEach((b2) -> {
            b2.render(mouseX, mouseY);
         });
         RenderUtil.post();
         GlStateManager.popMatrix();
      }

      if (this.drag) {
         if (!Mouse.isButtonDown(0)) {
            this.drag = false;
         }

         this.x = mouseX - this.dragX;
         this.y = mouseY - this.dragY;
         ((Button)this.buttons.get(0)).y = this.y + 22 - this.scroll;

         Button b4;
         for(var7 = this.buttons.iterator(); var7.hasNext(); b4.x = this.x + 5) {
            b4 = (Button)var7.next();
         }
      }

   }

   public void key(char typedChar, int keyCode) {
      this.buttons.forEach((b2) -> {
         b2.key(typedChar, keyCode);
      });
   }

   public void mouseScroll(int mouseX, int mouseY, int amount) {
      if (mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + 17 + this.expand) {
         this.scrollTo = (int)((float)this.scrollTo - (float)(amount / 120 * 28));
      }

   }

   public void click(int mouseX, int mouseY, int button) {
      if (mouseX > this.x - 2 && mouseX < this.x + 92 && mouseY > this.y - 2 && mouseY < this.y + 17) {
         if (button == 1) {
            this.extended = !this.extended;
            boolean var4 = this.extended;
         }

         if (button == 0) {
            this.drag = true;
            this.dragX = mouseX - this.x;
            this.dragY = mouseY - this.y;
         }
      }

      if (this.extended) {
         this.buttons.stream().filter((b2) -> {
            return b2.y < this.y + this.expand;
         }).forEach((b2) -> {
            b2.click(mouseX, mouseY, button);
         });
      }

   }
}
