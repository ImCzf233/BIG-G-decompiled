package me.bigg.screen.click2;

import java.awt.Color;
import me.bigg.value.Value;
import org.lwjgl.input.Mouse;

public class ColorValueButton extends ValueButton {
   private float[] hue = new float[]{0.0F};
   private int position;
   private int color = (new Color(125, 125, 125)).getRGB();

   public ColorValueButton(int x, int y) {
      super((Value)null, x, y);
      this.custom = true;
      this.position = -1111;
   }

   public void render(int mouseX, int mouseY) {
      float[] huee = new float[]{this.hue[0]};

      for(int i = this.x - 7; i < this.x - 7 + 85; ++i) {
         int color = Color.getHSBColor(huee[0] / 255.0F, 0.7F, 0.7F).getRGB();
         if (mouseX > i - 1 && mouseX < i + 1 && mouseY > this.y - 6 && mouseY < this.y + 12 && Mouse.isButtonDown(0)) {
            this.color = color;
            this.position = i;
         }

         if (this.color == color) {
            this.position = i;
         }

         huee[0] += 4.0F;
         if (huee[0] > 255.0F) {
            huee[0] -= 255.0F;
         }
      }

      if (this.hue[0] > 255.0F) {
         this.hue[0] -= 255.0F;
      }

   }

   public void key(char typedChar, int keyCode) {
   }

   public void click(int mouseX, int mouseY, int button) {
   }
}
