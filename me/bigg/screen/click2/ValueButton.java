package me.bigg.screen.click2;

import java.awt.Color;
import me.bigg.Client;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.util.RenderUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import org.lwjgl.input.Mouse;

public class ValueButton {
   public Value value;
   public String name;
   public boolean custom = false;
   public boolean change;
   public int x;
   public int y;
   public double opacity = 0.0;
   CFontRenderer font;

   public ValueButton(Value value, int x, int y) {
      this.font = Client.INSTANCE.getFontManager().arial12;
      this.value = value;
      this.x = x;
      this.y = y;
      this.name = "";
      if (this.value instanceof BoolValue) {
         this.change = (Boolean)((BoolValue)this.value).getValue();
      } else if (this.value instanceof EnumValue) {
         this.name = "" + ((EnumValue)this.value).getValue();
      } else if (value instanceof NumValue) {
         NumValue v = (NumValue)value;
         this.name = String.valueOf(this.name) + ((Number)v.getValue()).doubleValue();
      }

      this.opacity = 0.0;
   }

   public void render(int mouseX, int mouseY) {
      if (!this.custom) {
         if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + this.font.getStringHeight(this.value.getName()) + 5) {
            if (this.opacity + 10.0 < 200.0) {
               this.opacity += 10.0;
            } else {
               this.opacity = 200.0;
            }
         } else if (this.opacity - 6.0 > 0.0) {
            this.opacity -= 6.0;
         } else {
            this.opacity = 0.0;
         }

         RenderUtil.drawRect((double)(this.x - 9), (double)(this.y - 5), (double)(this.x - 9 + 88), (double)(this.y + this.font.getStringHeight(this.value.getName()) + 5), (new Color(120, 120, 120, (int)this.opacity)).getRGB());
         if (this.change) {
            Color color = new Color((new Color(180, 180, 180)).getRGB());
            RenderUtil.drawRect((double)(this.x - 9), (double)(this.y - 5), (double)(this.x - 9 + 88), (double)(this.y + this.font.getStringHeight(this.value.getName()) + 5), (new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(200.0 - this.opacity))).getRGB());
         }

         NumValue v;
         double min;
         if (this.value instanceof BoolValue) {
            this.change = (Boolean)((BoolValue)this.value).getValue();
         } else if (this.value instanceof EnumValue) {
            this.name = "" + ((EnumValue)this.value).getValue();
         } else if (this.value instanceof NumValue) {
            v = (NumValue)this.value;
            this.name = "" + ((Number)v.getValue()).doubleValue();
            if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + this.font.getStringHeight(this.value.getName()) + 5 && Mouse.isButtonDown(0)) {
               min = v.getMinimum().doubleValue();
               double max = v.getMaximum().doubleValue();
               double inc = v.getIncrement().doubleValue();
               double valAbs = (double)mouseX - ((double)this.x + 1.0);
               double perc = valAbs / 68.0;
               perc = Math.min(Math.max(0.0, perc), 1.0);
               double valRel = (max - min) * perc;
               double val = min + valRel;
               val = (double)Math.round(val * (1.0 / inc)) / (1.0 / inc);
               v.setValue(val);
            }
         }

         if (this.value instanceof NumValue) {
            v = (NumValue)this.value;
            min = (double)(68.0F * (((Number)v.getValue()).floatValue() - v.getMinimum().floatValue()) / (v.getMaximum().floatValue() - v.getMinimum().floatValue()));
            RenderUtil.drawRect((double)((float)this.x), (double)((float)(this.y + this.font.getStringHeight(this.value.getName()) + 3)), (double)((float)((double)this.x + min + 6.5)), (double)((float)(this.y + this.font.getStringHeight(this.value.getName()) + 4)), (new Color(255, 255, 255)).getRGB());
         }

         RenderUtil.drawRect(0.0, 0.0, 0.0, 0.0, 0);
         this.font.drawStringWithShadow(this.value.getName(), (double)this.x, (double)this.y, -1);
         this.font.drawStringWithShadow(this.name, (double)(this.x + 75 - this.font.getStringWidth(this.name)), (double)this.y, -1);
      }

   }

   public void key(char typedChar, int keyCode) {
   }

   public void click(int mouseX, int mouseY, int button) {
      if (!this.custom && mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6 && mouseY < this.y + this.font.getStringHeight(this.value.getName()) + 5) {
         if (this.value instanceof BoolValue) {
            BoolValue v = (BoolValue)this.value;
            v.setValue(!(Boolean)v.getValue());
            return;
         }

         if (this.value instanceof EnumValue) {
            EnumValue m = (EnumValue)this.value;
            Enum current = (Enum)m.getValue();
            int next = current.ordinal() + 1 >= m.getModes().length ? 0 : current.ordinal() + 1;
            this.value.setValue(m.getModes()[next]);
         }
      }

   }
}
