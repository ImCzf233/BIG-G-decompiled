package me.bigg.screen.click2;

import java.awt.Color;
import me.bigg.Client;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.module.Module;
import me.bigg.value.Value;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public class KeyBindButton extends ValueButton {
   public Module cheat;
   public double opacity = 0.0;
   public boolean bind;
   CFontRenderer font;

   public KeyBindButton(Module cheat, int x, int y) {
      super((Value)null, x, y);
      this.font = Client.INSTANCE.getFontManager().arial12;
      this.custom = true;
      this.bind = false;
      this.cheat = cheat;
   }

   public void render(int mouseX, int mouseY) {
      double d = mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + this.font.getStringHeight(this.cheat.getName()) + 5 ? (this.opacity + 10.0 < 200.0 ? (this.opacity += 10.0) : 200.0) : (this.opacity - 6.0 > 0.0 ? (this.opacity -= 6.0) : 0.0);
      this.opacity = d;
      Gui.drawRect(this.x - 9, this.y - 5, this.x - 9 + 88, this.y + this.font.getStringHeight(this.cheat.getName()) + 5, (new Color(255, 255, 255, (int)this.opacity)).getRGB());
      this.font.drawStringWithShadow("Key", (double)this.x, (double)this.y, -1);
      this.font.drawStringWithShadow(String.valueOf(String.valueOf(this.bind ? "" : "")) + Keyboard.getKeyName(this.cheat.getKeybinding()), (double)(this.x + 75 - this.font.getStringWidth(Keyboard.getKeyName(this.cheat.getKeybinding()))), (double)this.y, -1);
   }

   public void key(char typedChar, int keyCode) {
      if (this.bind) {
         this.cheat.setKeybinding(keyCode);
         if (keyCode == 1) {
            this.cheat.setKeybinding(0);
         }

         ClickyUI.binding = false;
         this.bind = false;
      }

      super.key(typedChar, keyCode);
   }

   public void click(int mouseX, int mouseY, int button) {
      if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + this.font.getStringHeight(this.cheat.getName()) + 5 && button == 0) {
         this.bind = !this.bind;
         ClickyUI.binding = this.bind;
      }

      super.click(mouseX, mouseY, button);
   }
}
