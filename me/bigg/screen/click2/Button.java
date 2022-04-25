package me.bigg.screen.click2;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.module.Module;
import me.bigg.value.Value;
import net.minecraft.client.gui.Gui;

public class Button {
   public Module cheat;
   public Window parent;
   public int x;
   public int y;
   public int index;
   public int remander;
   public double opacity = 0.0;
   public ArrayList buttons = Lists.newArrayList();
   public boolean expand;
   CFontRenderer font;

   public Button(Module cheat, int x, int y) {
      this.font = Client.INSTANCE.getFontManager().arial12;
      this.cheat = cheat;
      this.x = x;
      this.y = y;
      int y2 = y + 14;
      if (cheat == Client.INSTANCE.getModuleManager().getModule("hud")) {
         this.buttons.add(new ColorValueButton(x + 15, y2));
      }

      for(Iterator var6 = cheat.getValues().iterator(); var6.hasNext(); y2 += 15) {
         Value v = (Value)var6.next();
         this.buttons.add(new ValueButton(v, x + 5, y2));
      }

      this.buttons.add(new KeyBindButton(cheat, x + 5, y2));
   }

   public void render(int mouseX, int mouseY) {
      int i;
      if (this.cheat.getValues().size() + (this.cheat == Client.INSTANCE.getModuleManager().getModule("hud") ? 2 : 1) != this.buttons.size()) {
         this.buttons.clear();
         i = this.y + 14;
         if (this.cheat == Client.INSTANCE.getModuleManager().getModule("hud")) {
            this.buttons.add(new ColorValueButton(this.x + 15, i));
         }

         for(Iterator var5 = this.cheat.getValues().iterator(); var5.hasNext(); i += 15) {
            Value v = (Value)var5.next();
            this.buttons.add(new ValueButton(v, this.x + 5, i));
         }

         this.buttons.add(new KeyBindButton(this.cheat, this.x + 5, i));
      }

      if (this.index != 0) {
         Button b2 = (Button)this.parent.buttons.get(this.index - 1);
         this.y = b2.y + 15 + (b2.expand ? 15 * b2.buttons.size() : 0);
      }

      for(i = 0; i < this.buttons.size(); ++i) {
         ((ValueButton)this.buttons.get(i)).y = this.y + 14 + 15 * i;
         ((ValueButton)this.buttons.get(i)).x = this.x + 5;
      }

      double d = mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + this.font.getStringHeight(this.cheat.getName()) + 4 ? (this.opacity + 10.0 < 200.0 ? (this.opacity += 10.0) : 200.0) : (this.opacity - 6.0 > 0.0 ? (this.opacity -= 6.0) : 0.0);
      this.opacity = d;
      Gui.drawRect(this.x - 4, this.y - 6, this.x - 6 + 89, this.y + this.font.getStringHeight(this.cheat.getName()) + 6, (new Color(120, 120, 120, (int)this.opacity)).getRGB());
      if (this.cheat.isEnabled()) {
         Color color = new Color((new Color(30, 30, 30)).getRGB());
         Gui.drawRect(this.x - 4, this.y - 6, this.x - 6 + 89, this.y + this.font.getStringHeight(this.cheat.getName()) + 6, (new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(200.0 - this.opacity))).getRGB());
      }

      this.font.drawStringWithShadow(this.cheat.getName(), (double)this.x, (double)this.y, -1);
      if (!this.expand && this.buttons.size() > 1) {
         Gui.drawRect(this.x - 6 + 89, this.y - 5, this.x - 6 + 90, this.y + this.font.getStringHeight(this.cheat.getName()) + 6, (new Color((new Color(200, 200, 200)).getRGB())).getRGB());
      }

      if (this.expand) {
         this.buttons.forEach((b) -> {
            b.render(mouseX, mouseY);
         });
      }

   }

   public void key(char typedChar, int keyCode) {
      this.buttons.forEach((b) -> {
         b.key(typedChar, keyCode);
      });
   }

   public void click(int mouseX, int mouseY, int button) {
      if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + this.font.getStringHeight(this.cheat.getName()) + 4) {
         if (button == 0) {
            this.cheat.setEnable(!this.cheat.isEnabled());
         }

         if (button == 1 && !this.buttons.isEmpty()) {
            this.expand = !this.expand;
            boolean var4 = this.expand;
         }
      }

      if (this.expand) {
         this.buttons.forEach((b) -> {
            b.click(mouseX, mouseY, button);
         });
      }

   }

   public void setParent(Window parent) {
      this.parent = parent;

      for(int i = 0; i < this.parent.buttons.size(); ++i) {
         if (this.parent.buttons.get(i) == this) {
            this.index = i;
            this.remander = this.parent.buttons.size() - i;
            break;
         }
      }

   }
}
