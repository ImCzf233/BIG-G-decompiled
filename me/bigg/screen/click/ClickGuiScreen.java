package me.bigg.screen.click;

import java.awt.Color;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.vision.CGui;
import me.bigg.module.impl.vision.HUD;
import me.bigg.util.ColorUtil;
import me.bigg.util.MathUtil;
import me.bigg.util.RenderUtil;
import me.bigg.util.TranslateUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class ClickGuiScreen extends GuiScreen {
   private static boolean exiting;
   private int guiWidth;
   private int guiHeight;
   private int x;
   private int y;
   private int dragX;
   private int dragY;
   private boolean dragging;
   private boolean scale;
   private final TranslateUtil startAnimation = new TranslateUtil(0.0F, 0.0F);
   private final TranslateUtil wheelAnimation = new TranslateUtil(0.0F, 0.0F);
   private float wheel;
   private Category currentCategory;
   private ArrayList extendedModuleList = new ArrayList();
   Cursor emptyCursor;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$vision$CGui$grilleEnums;

   public ClickGuiScreen() {
      this.startAnimation.setX(0.0F);
      this.startAnimation.setY(0.0F);
      this.guiWidth = Client.INSTANCE.getClickGuiWidth();
      this.guiHeight = Client.INSTANCE.getClickGuiHeight();
      this.dragging = false;
      this.scale = false;
      this.x = Client.INSTANCE.getClickGuiX();
      this.y = Client.INSTANCE.getClickGuiY();
      this.wheel = Client.INSTANCE.getClickWheel();
      this.currentCategory = Client.INSTANCE.getClickCategory();
      this.extendedModuleList = Client.INSTANCE.getClickModuleList();
      this.dragX = 0;
      this.dragY = 0;
      this.hideCursor();
   }

   public void initGui() {
      super.initGui();
      if (this.x + this.guiWidth > this.width) {
         this.x = 5;
      }

      if (this.y + this.guiHeight > this.height) {
         this.y = 5;
      }

      if (this.x + this.guiWidth > this.width) {
         this.guiWidth = 310;
      }

      if (this.y + this.guiHeight > this.height) {
         this.guiHeight = 200;
      }

      this.dragging = false;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (!Mouse.isButtonDown(0) && this.dragging) {
         this.dragging = false;
      }

      if (!Mouse.isButtonDown(0) && this.scale) {
         this.scale = false;
      }

      if (this.dragging) {
         this.x = mouseX - this.dragX;
         this.y = mouseY - this.dragY;
      } else if (this.scale) {
         this.guiWidth = Math.max(mouseX - this.x, 310);
         this.guiHeight = Math.max(mouseY - this.y, 200);
      }

      int value;
      int rainbowTick;
      if ((Boolean)CGui.getBottomValue().getValue()) {
         value = 31 - ((Number)CGui.getGradientValue().getValue()).intValue();

         for(rainbowTick = 0; rainbowTick < this.width; rainbowTick += value) {
            Color c = ColorUtil.getRainbow(10.0F, 1.0F, 0.5F, (long)rainbowTick * (long)(31 - value));
            RenderUtil.drawVerticalGradientSideways((double)rainbowTick, (double)((float)this.height - this.wheelAnimation.getX()), (double)(rainbowTick + value), (double)this.height, 15, (new Color(c.getRed(), c.getGreen(), c.getBlue(), 130)).getRGB());
         }
      }

      int moduleY;
      value = Math.round(this.startAnimation.getX());
      rainbowTick = Math.round(this.startAnimation.getY());
      RenderUtil.startGlScissor(this.x, this.y, value, rainbowTick);
      drawRect(this.x, this.y, this.x + this.guiWidth, this.y + this.guiHeight, (new Color(7, 7, 7)).getRGB());
      label253:
      switch ($SWITCH_TABLE$me$bigg$module$impl$vision$CGui$grilleEnums()[((CGui.grilleEnums)CGui.getGrilleValue().getMode()).ordinal()]) {
         case 2:
            moduleY = 0;

            while(true) {
               if (moduleY >= this.guiWidth) {
                  break label253;
               }

               drawRect(this.x + moduleY, this.y, this.x + moduleY + 1, this.y + this.guiHeight, ColorUtil.reAlpha(HUD.getCustomColor(), 30).getRGB());
               moduleY += 3;
            }
         case 3:
            for(moduleY = 0; moduleY < this.guiWidth; moduleY += 3) {
               drawRect(this.x + moduleY, this.y, this.x + moduleY + 1, this.y + this.guiHeight, ColorUtil.getRainbow(7.0F, 1.0F, 0.1F, (long)moduleY * 2L).getRGB());
            }
      }

      moduleY = (int)this.wheelAnimation.getY();
      Iterator var8 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

      while(true) {
         Module m;
         int onceHeight;
         boolean hoverOnBool;
         do {
            if (!var8.hasNext()) {
               drawRect(this.x, this.y, this.x + this.guiWidth, this.y + 10, (new Color(10, 10, 15)).getRGB());
               RenderUtil.drawVerticalGradientSideways((double)(this.x + 90), (double)(this.y + 10), (double)(this.x + this.guiWidth), (double)(this.y + 15), (new Color(20, 20, 20, 100)).getRGB(), 15);
               drawRect(this.x, this.y, this.x + 90, this.y + this.guiHeight, (new Color(10, 10, 15)).getRGB());
               RenderUtil.drawHorizontalGradientSideways((double)(this.x + 90), (double)this.y, (double)(this.x + 95), (double)(this.y + this.guiHeight), (new Color(20, 20, 20, 100)).getRGB(), 15);
               float var10002 = (float)(this.x + 45);
               Client.INSTANCE.getFontManager().logo32.drawCenteredString("PIGP", var10002, (float)(this.y + 20), (new Color(200, 200, 200)).getRGB());
               int categoryHeight = 0;
               Category[] var38;
               onceHeight = (var38 = Category.values()).length;

               for(int var36 = 0; var36 < onceHeight; ++var36) {
                  Category category = var38[var36];
                  String name = (this.currentCategory == category ? "§f" : "") + category.name();
                  hoverOnBool = MathUtil.inRange((double)mouseX, (double)mouseY, (double)((float)(this.x + 47) + (float)Client.INSTANCE.getFontManager().arial18.getStringWidth(name) / 2.0F), (double)((float)this.y + (float)this.guiHeight / 2.0F - 35.0F + 5.0F + (float)categoryHeight + 8.0F), (double)((float)(this.x + 43) - (float)Client.INSTANCE.getFontManager().arial18.getStringWidth(name) / 2.0F), (double)((float)this.y + (float)this.guiHeight / 2.0F - 35.0F + 1.0F + (float)categoryHeight));
                  var10002 = (float)(this.x + 45);
                  float var10003 = (float)this.y + (float)this.guiHeight / 2.0F - 35.0F + 5.0F + (float)categoryHeight;
                  Client.INSTANCE.getFontManager().arial18.drawCenteredString(name, var10002, var10003, (new Color(hoverOnBool ? 130 : 150, hoverOnBool ? 130 : 150, hoverOnBool ? 130 : 150)).getRGB());
                  categoryHeight += 14;
               }

               if ((Boolean)CGui.getBorderValue().getValue()) {
                  drawRect(this.x, this.y, this.x + 1, this.y + this.guiHeight, HUD.getCustomColor().getRGB());
                  drawRect(this.x, this.y, this.x + this.guiWidth, this.y + 1, HUD.getCustomColor().getRGB());
                  drawRect(this.x + this.guiWidth - 1, this.y, this.x + this.guiWidth, this.y + this.guiHeight, HUD.getCustomColor().getRGB());
                  drawRect(this.x, this.y + this.guiHeight - 1, this.x + this.guiWidth, this.y + this.guiHeight, HUD.getCustomColor().getRGB());
               }

               RenderUtil.stopGlScissor();
               int real = Mouse.getDWheel();
               float moduleHeight = (float)moduleY - this.wheelAnimation.getY();
               if (Mouse.hasWheel() && MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth), (double)(this.y + this.guiHeight), (double)this.x, (double)this.y)) {
                  if (real > 0 && this.wheel < 0.0F) {
                     for(onceHeight = 0; onceHeight < 5 && this.wheel < 0.0F; ++onceHeight) {
                        this.wheel += 5.0F;
                     }
                  } else {
                     for(onceHeight = 0; onceHeight < 5 && real < 0 && moduleHeight > (float)this.guiHeight && Math.abs(this.wheel) < moduleHeight - (float)(this.guiHeight - 12); ++onceHeight) {
                        this.wheel -= 5.0F;
                     }
                  }
               }

               this.startAnimation.interpolate((float)(exiting ? 0 : this.guiWidth), (float)(exiting ? 0 : this.guiHeight), this.scale ? 1.0F : (exiting ? 0.4F : 0.2F));
               this.wheelAnimation.interpolate((float)(exiting ? 0 : 120), this.wheel, exiting ? 0.4F : 0.2F);
               if (exiting) {
                  if (Client.INSTANCE.getClickGuiX() != this.x) {
                     Client.INSTANCE.setClickGuiX(this.x);
                  }

                  if (Client.INSTANCE.getClickGuiY() != this.y) {
                     Client.INSTANCE.setClickGuiY(this.y);
                  }

                  if (Client.INSTANCE.getClickGuiWidth() != this.x) {
                     Client.INSTANCE.setClickGuiWidth(this.guiWidth);
                  }

                  if (Client.INSTANCE.getClickGuiHeight() != this.y) {
                     Client.INSTANCE.setClickGuiHeight(this.guiHeight);
                  }

                  if (Client.INSTANCE.getClickWheel() != this.wheel) {
                     Client.INSTANCE.setClickWheel(this.wheel);
                  }

                  if (Client.INSTANCE.getClickCategory() != this.currentCategory) {
                     Client.INSTANCE.setClickCategory(this.currentCategory);
                  }

                  Client.INSTANCE.setClickModuleList(this.extendedModuleList);
                  Client.INSTANCE.getFileManager().saveClickGuiInfo();
                  if (Display.isActive() && !this.mc.inGameHasFocus) {
                     this.mc.inGameHasFocus = true;
                     this.mc.mouseHelper.grabMouseCursor();
                  }

                  if (this.startAnimation.getX() == this.startAnimation.getY() && this.startAnimation.getX() == 0.0F) {
                     this.mc.displayGuiScreen((GuiScreen)null);
                  }
               } else {
                  boolean onScale = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth), (double)(this.y + this.guiHeight), (double)(this.x + this.guiWidth - 7), (double)(this.y + this.guiHeight - 7));
                  GlStateManager.pushMatrix();
                  GlStateManager.enableBlend();
                  this.mc.getTextureManager().bindTexture(new ResourceLocation("annex/arrow.png"));
                  int size = 16;
                  Color c = onScale ? ColorUtil.getDarker(HUD.getCustomColor(), 50, 255) : HUD.getCustomColor();
                  GlStateManager.color((float)c.getRed() * 0.003921569F, (float)c.getGreen() * 0.003921569F, (float)c.getBlue() * 0.003921569F);
                  drawModalRectWithCustomSizedTexture(mouseX, mouseY, 0.0F, 0.0F, size, size, (float)size, (float)size);
                  GlStateManager.resetColor();
                  GlStateManager.disableBlend();
                  GlStateManager.popMatrix();
               }

               return;
            }

            m = (Module)var8.next();
         } while(m.getCategory() != this.currentCategory);

         boolean debugging = this.extendedModuleList.contains(m);
         drawRect(this.x + 95, this.y + 15 + moduleY, this.x + this.guiWidth - 5, this.y + 39 + moduleY, (new Color(15, 15, 25)).getRGB());
         drawRect(this.x + 95, this.y + 15 + moduleY, this.x + 97, this.y + 39 + moduleY, m.isEnabled() ? -1 : (new Color(130, 130, 130)).getRGB());
         Client.INSTANCE.getFontManager().arial18.drawString(m.getName(), (float)(this.x + 100), (float)(this.y + 20 + moduleY), m.isEnabled() ? -1 : (new Color(130, 130, 130)).getRGB());
         Client.INSTANCE.getFontManager().arial14.drawString(m.getIntroduce(), (float)(this.x + 100), (float)(this.y + 32 + moduleY), m.isEnabled() ? -1 : (new Color(130, 130, 130)).getRGB());
         if (debugging) {
            onceHeight = 20;
            drawRect(this.x + 97, this.y + 39 + moduleY, this.x + this.guiWidth - 7, this.y + 42 + moduleY + m.getValues().size() * onceHeight, (new Color(15, 15, 25)).getRGB());
            RenderUtil.drawVerticalGradientSideways((double)(this.x + 97), (double)(this.y + 39 + moduleY), (double)(this.x + this.guiWidth - 7), (double)(this.y + 44 + moduleY), (new Color(0, 0, 0, 100)).getRGB(), 15);

            for(Iterator var12 = m.getValues().iterator(); var12.hasNext(); moduleY += onceHeight) {
               Value value = (Value)var12.next();
               Client.INSTANCE.getFontManager().arial16.drawString(value.getName(), (float)(this.x + 100), (float)(this.y + 45 + moduleY), -1);
               Client.INSTANCE.getFontManager().arial14.drawString(value.getIntroduce(), (float)(this.x + 100), (float)(this.y + 53 + moduleY), -1);
               if (value instanceof EnumValue) {
                  hoverOnBool = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth - 10), (double)(this.y + 54 + moduleY), (double)(this.x + this.guiWidth - 90), (double)(this.y + 44 + moduleY));
                  drawRect(this.x + this.guiWidth - 90, this.y + 44 + moduleY, this.x + this.guiWidth - 10, this.y + 54 + moduleY, (new Color(0, 0, 0, 150)).getRGB());
                  Client.INSTANCE.getFontManager().arial14.drawCenteredString(((EnumValue)value).getMode().name(), (float)(this.x + this.guiWidth - 50), (float)(this.y + 48 + moduleY), -1);
                  if (hoverOnBool) {
                     drawRect(this.x + this.guiWidth - 90, this.y + 44 + moduleY, this.x + this.guiWidth - 10, this.y + 54 + moduleY, (new Color(0, 0, 0, 80)).getRGB());
                  }
               }

               if (value instanceof BoolValue) {
                  hoverOnBool = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth - 10), (double)(this.y + 54 + moduleY), (double)(this.x + this.guiWidth - 20), (double)(this.y + 44 + moduleY));
                  drawRect(this.x + this.guiWidth - 20, this.y + 44 + moduleY, this.x + this.guiWidth - 10, this.y + 54 + moduleY, (new Color(0, 0, 0, 150)).getRGB());
                  if ((Boolean)value.getValue()) {
                     drawRect(this.x + this.guiWidth - 19, this.y + 45 + moduleY, this.x + this.guiWidth - 11, this.y + 53 + moduleY, HUD.getCustomColor().getRGB());
                  }

                  if (hoverOnBool) {
                     drawRect(this.x + this.guiWidth - 20, this.y + 44 + moduleY, this.x + this.guiWidth - 10, this.y + 54 + moduleY, (new Color(0, 0, 0, 80)).getRGB());
                  }
               }

               if (value instanceof NumValue) {
                  NumValue s = (NumValue)value;
                  double inc = s.getIncrement().doubleValue();
                  double max = s.getMaximum().doubleValue();
                  double min = s.getMinimum().doubleValue();
                  double now = ((Number)s.getValue()).doubleValue();
                  Client.INSTANCE.getFontManager().arial14.drawString(String.valueOf(now), (float)(this.x + this.guiWidth - 109), (float)(this.y + 45 + moduleY), -1);
                  int rectWidth = this.x + this.guiWidth - 10 - (this.x + this.guiWidth - 110);
                  drawRect(this.x + this.guiWidth - 110, this.y + 52 + moduleY, this.x + this.guiWidth - 10, this.y + 56 + moduleY, (new Color(0, 0, 0, 150)).getRGB());
                  drawRect(this.x + this.guiWidth - 110, this.y + 52 + moduleY, (int)((double)(this.x + this.guiWidth - 110) + (double)rectWidth * (now - min) / (max - min)), this.y + 56 + moduleY, HUD.getCustomColor().getRGB());
                  boolean hoverOnNum = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth - 10 + 4), (double)(this.y + 56 + moduleY + 4), (double)(this.x + this.guiWidth - 110 - 4), (double)(this.y + 51 + moduleY - 4));
                  if (hoverOnNum) {
                     drawRect(this.x + this.guiWidth - 110, this.y + 52 + moduleY, this.x + this.guiWidth - 10, this.y + 56 + moduleY, (new Color(0, 0, 0, 80)).getRGB());
                     if (Mouse.isButtonDown(0)) {
                        double valAbs = (double)(mouseX - (this.x + this.guiWidth - 110));
                        double perc = valAbs / ((double)rectWidth * Math.max(Math.min(now / max, 0.0), 1.0));
                        perc = Math.min(Math.max(0.0, perc), 1.0);
                        double valRel = (max - min) * perc;
                        double val = min + valRel;
                        val = (double)Math.round(val * (1.0 / inc)) / (1.0 / inc);
                        s.setValue(val);
                     }
                  }
               }
            }
         }

         moduleY += 27;
      }
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      boolean onMove = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth), (double)(this.y + 10), (double)this.x, (double)this.y);
      boolean onScale = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth), (double)(this.y + this.guiHeight), (double)(this.x + this.guiWidth - 7), (double)(this.y + this.guiHeight - 7));
      if (onMove) {
         if (mouseButton == 0) {
            this.dragX = mouseX - this.x;
            this.dragY = mouseY - this.y;
            this.dragging = true;
         }
      } else if (onScale) {
         if (mouseButton == 0) {
            this.scale = true;
         }
      } else {
         int moduleY;
         if (mouseButton == 0) {
            moduleY = 0;
            Category[] var10;
            int var9 = (var10 = Category.values()).length;

            for(int var8 = 0; var8 < var9; ++var8) {
               Category category = var10[var8];
               String name = (this.currentCategory == category ? "> " : "") + category.name();
               boolean onCategory = MathUtil.inRange((double)mouseX, (double)mouseY, (double)((float)(this.x + 47) + (float)Client.INSTANCE.getFontManager().arial18.getStringWidth(name) / 2.0F), (double)((float)this.y + (float)this.guiHeight / 2.0F - 35.0F + 5.0F + (float)moduleY + 8.0F), (double)((float)(this.x + 43) - (float)Client.INSTANCE.getFontManager().arial18.getStringWidth(name) / 2.0F), (double)((float)this.y + (float)this.guiHeight / 2.0F - 35.0F + 1.0F + (float)moduleY));
               if (this.currentCategory != category && onCategory) {
                  this.currentCategory = category;
                  this.wheelAnimation.setY(0.0F);
                  this.wheel = 0.0F;
                  return;
               }

               moduleY += 14;
            }
         }

         moduleY = (int)this.wheelAnimation.getY();
         Iterator var19 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(true) {
            Module m;
            do {
               if (!var19.hasNext()) {
                  return;
               }

               m = (Module)var19.next();
            } while(m.getCategory() != this.currentCategory);

            boolean debugging = this.extendedModuleList.contains(m);
            boolean hoverOnM = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth - 5), (double)(this.y + 39 + moduleY), (double)(this.x + 95), (double)(this.y + 15 + moduleY));
            if (hoverOnM) {
               if (mouseButton == 0) {
                  m.toggle();
               } else if (mouseButton == 1) {
                  if (this.extendedModuleList.contains(m)) {
                     this.extendedModuleList.remove(m);
                  } else {
                     this.extendedModuleList.add(m);
                  }
               }
            }

            if (debugging) {
               int onceHeight = 20;
               drawRect(this.x + 97, this.y + 39 + moduleY, this.x + this.guiWidth - 7, this.y + 42 + moduleY + m.getValues().size() * onceHeight, (new Color(40, 40, 40)).getRGB());
               RenderUtil.drawVerticalGradientSideways((double)(this.x + 97), (double)(this.y + 39 + moduleY), (double)(this.x + this.guiWidth - 7), (double)(this.y + 44 + moduleY), (new Color(0, 0, 0, 100)).getRGB(), 15);

               for(Iterator var13 = m.getValues().iterator(); var13.hasNext(); moduleY += onceHeight) {
                  Value value = (Value)var13.next();
                  Client.INSTANCE.getFontManager().arial16.drawString(value.getName(), (float)(this.x + 100), (float)(this.y + 45 + moduleY), -1);
                  Client.INSTANCE.getFontManager().arial14.drawString(value.getIntroduce(), (float)(this.x + 100), (float)(this.y + 53 + moduleY), -1);
                  boolean hoverOnBool;
                  if (value instanceof EnumValue) {
                     EnumValue s = (EnumValue)value;
                     hoverOnBool = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth - 10), (double)(this.y + 54 + moduleY), (double)(this.x + this.guiWidth - 90), (double)(this.y + 44 + moduleY));
                     if (hoverOnBool) {
                        Enum current;
                        if (mouseButton == 1) {
                           current = (Enum)s.getValue();
                           s.setValue(s.getModes()[current.ordinal() - 1 < 0 ? s.getModes().length - 1 : current.ordinal() - 1]);
                        }

                        if (mouseButton == 0) {
                           current = (Enum)s.getValue();
                           int next = current.ordinal() + 1 >= s.getModes().length ? 0 : current.ordinal() + 1;
                           s.setValue(s.getModes()[next]);
                        }
                     }
                  }

                  if (value instanceof BoolValue) {
                     BoolValue bool = (BoolValue)value;
                     hoverOnBool = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth - 10), (double)(this.y + 54 + moduleY), (double)(this.x + this.guiWidth - 20), (double)(this.y + 44 + moduleY));
                     if (hoverOnBool) {
                        bool.setValue(!(Boolean)bool.getValue());
                     }
                  }
               }
            }

            moduleY += 27;
         }
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      boolean onMove = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth), (double)(this.y + 10), (double)this.x, (double)this.y);
      boolean onScale = MathUtil.inRange((double)mouseX, (double)mouseY, (double)(this.x + this.guiWidth), (double)(this.y + this.guiHeight), (double)(this.x + this.guiWidth - 7), (double)(this.y + this.guiHeight - 7));
      if (onMove) {
         if (state == 0) {
            this.dragX = mouseX - this.x;
            this.dragY = mouseY - this.y;
            this.dragging = false;
         }
      } else if (onScale && state == 0) {
         this.scale = false;
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
      Client.INSTANCE.getFileManager().saveEnabled();
      Client.INSTANCE.getFileManager().saveValue();

      try {
         Mouse.setNativeCursor((Cursor)null);
      } catch (Throwable var2) {
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   private void hideCursor() {
      if (this.emptyCursor == null) {
         if (Mouse.isCreated()) {
            int min = Cursor.getMinCursorSize();
            IntBuffer tmp = BufferUtils.createIntBuffer(min * min);

            try {
               this.emptyCursor = new Cursor(min, min, min / 2, min / 2, 1, tmp, (IntBuffer)null);
            } catch (LWJGLException var5) {
               var5.printStackTrace();
            }
         } else {
            Client.INSTANCE.getLogger().warning("Could not create empty cursor before Mouse object is created");
         }
      }

      try {
         Mouse.setNativeCursor(Mouse.isInsideWindow() ? this.emptyCursor : null);
      } catch (LWJGLException var4) {
         var4.printStackTrace();
      }

   }

   public static boolean isExiting() {
      return exiting;
   }

   public static void setExiting(boolean exiting) {
      ClickGuiScreen.exiting = exiting;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$vision$CGui$grilleEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$vision$CGui$grilleEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[CGui.grilleEnums.values().length];

         try {
            var0[CGui.grilleEnums.HUD.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[CGui.grilleEnums.None.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[CGui.grilleEnums.Rainbow.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$vision$CGui$grilleEnums = var0;
         return var0;
      }
   }
}
