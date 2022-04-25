package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.event.KeyTypedEvent;
import me.bigg.event.OverlayEvent;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.ColorUtil;
import me.bigg.util.CompassUtil;
import me.bigg.util.RenderUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class HUD extends Module {
   private int indexStr;
   private boolean reverse;
   private final TimerUtil timer = new TimerUtil();
   private final TimerUtil lag = new TimerUtil();
   private final TimerUtil str = new TimerUtil();
   private final CompassUtil compass = new CompassUtil(325.0F, 325.0F, 1.0F, 2, true);
   private final BoolValue waterMarkValue = new BoolValue("Water Mark", "Water Mark on your hud", true);
   private final BoolValue pPolylineValue = new BoolValue("P Polyline", "Exhi woooooooah~~", false);
   private final BoolValue activeModValue = new BoolValue("Active Mod", "Active mods list on your hud", true);
   private final BoolValue sortValue = new BoolValue("Sort Display", "idk futures", true);
   private final BoolValue tabGuiValue = new BoolValue("Tab Gui", "Feels good", true);
   private final BoolValue hotbarValue = new BoolValue("Hot bar", "Just fucking hotbar", true);
   private final BoolValue compassValue = new BoolValue("Compass", "Jello be like", true);
   private final BoolValue rRectValue = new BoolValue("R Rect", "Right out border", true);
   private final BoolValue lRectValue = new BoolValue("L Rect", "Left out border", true);
   private final BoolValue bRectValue = new BoolValue("B Rect", "Bottom out border", true);
   private final EnumValue colorModeValue = new EnumValue("Color Mode", "HUD color mode", HUD.colorEnums.values());
   private final EnumValue markModeValue = new EnumValue("Mark Mode", "Water mark mode", HUD.markEnums.values());
   private static final NumValue differenceValue = new NumValue("Difference", "Rainbow difference", 10.0, 1.0, 20.0, 1.0);
   private static final NumValue secondValue = new NumValue("Second", "Rainbow second", 10.0, 1.0, 20.0, 1.0);
   private static final NumValue saturationValue = new NumValue("Saturation", "rainbow saturation", 1.0, 0.0, 1.0, 0.05);
   private static final NumValue brightValue = new NumValue("Bright", "Color bright", 1.0, 0.0, 1.0, 0.05);
   private static final NumValue redValue = new NumValue("Red", "Red color", 255.0, 0.0, 255.0, 5.0);
   private static final NumValue greenValue = new NumValue("Green", "Green color", 255.0, 0.0, 255.0, 5.0);
   private static final NumValue blueValue = new NumValue("Blue", "Blue color", 255.0, 0.0, 255.0, 5.0);
   private static final NumValue alphaValue = new NumValue("Alpha", "Array bg alpha", 100.0, 0.0, 255.0, 5.0);
   private int indexCategory;
   private int indexModule;
   private int indexValue;
   private Value debuggingValue;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$vision$HUD$markEnums;

   public HUD() {
      super("HUD", "Your hud", Category.Vision);
      this.setEnable(true);
      this.addValues(new Value[]{this.colorModeValue, this.markModeValue, this.waterMarkValue, this.pPolylineValue, this.activeModValue, this.sortValue, this.tabGuiValue, this.hotbarValue, this.compassValue, this.rRectValue, this.lRectValue, this.bRectValue, brightValue, secondValue, differenceValue, saturationValue, redValue, greenValue, blueValue, alphaValue});
   }

   public void onEnable() {
      super.onEnable();
      this.lag.reset();
      this.str.reset();
      this.timer.reset();
      this.debuggingValue = null;
      this.indexCategory = 0;
      this.indexModule = -1;
      this.indexValue = -1;
      this.indexStr = 0;
      this.reverse = false;
   }

   @EventTarget
   void onRenderOverlayEvent(OverlayEvent event) {
      if (!mc.gameSettings.showDebugInfo) {
         ScaledResolution sr = event.getScaledResolution();
         int staticColor;
         if (this.colorModeValue.getMode() == HUD.colorEnums.Rainbow) {
            staticColor = ColorUtil.getRainbow(((Number)secondValue.getValue()).floatValue(), ((Number)saturationValue.getValue()).floatValue(), ((Number)brightValue.getValue()).floatValue()).getRGB();
         } else if (this.colorModeValue.getMode() == HUD.colorEnums.Fade) {
            staticColor = ColorUtil.getFadeRainbow(new Color(((Number)redValue.getValue()).intValue(), ((Number)greenValue.getValue()).intValue(), ((Number)blueValue.getValue()).intValue()), 1, 16).getRGB();
         } else {
            staticColor = (new Color(((Number)redValue.getValue()).intValue(), ((Number)greenValue.getValue()).intValue(), ((Number)blueValue.getValue()).intValue())).getRGB();
         }

         if (this.str.hasPassed(600L)) {
            if (this.reverse) {
               --this.indexStr;
            } else {
               ++this.indexStr;
            }

            this.str.reset();
         }

         if (this.indexStr > Client.INSTANCE.getName().length()) {
            this.reverse = true;
            --this.indexStr;
         } else if (this.indexStr < 0 && this.reverse) {
            this.reverse = false;
            ++this.indexStr;
         }

         int posX = 3;
         int posY = 3;
         int y;
         float realCategoryY;
         if ((Boolean)this.waterMarkValue.getValue()) {
            String clientName = Client.INSTANCE.getName().charAt(0) + "§7" + Client.INSTANCE.getName().substring(1);
            switch ($SWITCH_TABLE$me$bigg$module$impl$vision$HUD$markEnums()[((markEnums)this.markModeValue.getMode()).ordinal()]) {
               case 1:
                  RenderUtil.drawShadow(posX + 4, posY + 4, Client.INSTANCE.getFontManager().logo32.getStringWidth(clientName), 20, 0.5F);
                  Client.INSTANCE.getFontManager().logo32.drawString(clientName, (float)(posX + 4), (float)(posY + 4), staticColor);
                  posY += 25;
                  break;
               case 2:
                  Client.INSTANCE.getFontManager().arial20.drawStringWithShadow(clientName, (double)(posX + 3), (double)(posY + 3), staticColor);
                  posY += 15;
                  break;
               case 3:
                  String name = this.indexStr == 0 ? "" : Client.INSTANCE.getName().substring(0, this.indexStr);
                  name = name.length() > 1 ? name.charAt(0) + "§7" + name.substring(1) : name;
                  Client.INSTANCE.getFontManager().arial20.drawStringWithShadow(name, (double)(posX + 3), (double)(posY + 3), staticColor);
                  posY += 15;
                  break;
               case 4:
                  Client.INSTANCE.getFontManager().arial18.drawStringWithShadow(clientName, (double)(posX + 1), (double)(posY + 1), staticColor);
                  posY += 10;
                  break;
               case 5:
                  String text = "B§7IGG §fb" + Client.INSTANCE.getBuild() + " | §7" + "Player: §f" + mc.thePlayer.getName() + "§f | §7" + "User: §f" + Client.USERNAME + "§f | §7" + (mc.isSingleplayer() ? "In §fSingle Player" : "In §f" + mc.getCurrentServerData().serverIP.toLowerCase()) + "§f | §7" + "Time: §f" + (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()) + "§f | §7" + "FPS: §f" + Minecraft.getDebugFPS();
                  y = mc.fontRendererObj.getStringWidth(text) / 2 + 10;
                  realCategoryY = 14.0F;
                  RenderUtil.drawRect((double)posX, (double)posY, (double)(posX + y), (double)((float)posY + realCategoryY), staticColor);
                  RenderUtil.drawRect((double)(posX + 1), (double)(posY + 1), (double)(posX + y - 1), (double)((float)posY + realCategoryY - 1.0F), (new Color(60, 60, 60)).getRGB());
                  RenderUtil.drawRect((double)(posX + 2), (double)(posY + 2), (double)(posX + y - 2), (double)((float)posY + realCategoryY - 2.0F), (new Color(15, 15, 15)).getRGB());
                  GlStateManager.pushMatrix();
                  GlStateManager.scale(0.5, 0.5, 0.5);
                  mc.fontRendererObj.drawString(text, (posX + 5) * 2, (posY + 5) * 2 + 1, staticColor);
                  GlStateManager.popMatrix();
                  posY = (int)((float)posY + realCategoryY + 3.0F);
            }
         }

         int roundedY;
         if ((Boolean)this.tabGuiValue.getValue()) {
            float cateYPos = 0.0F;
            float moduleYPos = 0.0F;
            float valueYPos = 0.0F;
            Category[] var12;
            int var11 = (var12 = Category.values()).length;

            for(int var34 = 0; var34 < var11; ++var34) {
               Category cate = var12[var34];
               RenderUtil.drawRect((double)posX, (double)((float)posY + cateYPos), (double)(posX + 60), (double)((float)posY + cateYPos + 11.5F), (new Color(4, 4, 4, 180)).getRGB());
               if (cate == Category.values()[this.indexCategory]) {
                  RenderUtil.drawRect((double)posX, (double)((float)posY + cateYPos), (double)(posX + 60), (double)((float)posY + cateYPos + 12.0F), staticColor);
               }

               Client.INSTANCE.getFontManager().arial18.drawStringWithShadow(cate.name(), (double)(posX + 2), (double)((float)posY + cateYPos + 3.0F), cate == Category.values()[this.indexCategory] ? -1 : (new Color(180, 180, 180)).getRGB());
               cateYPos += 11.5F;
            }

            y = posX + 61;
            if (this.indexModule != -1) {
               ArrayList modules = new ArrayList();
               Iterator var43 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

               while(var43.hasNext()) {
                  Module m = (Module)var43.next();
                  if (m.getCategory() == Category.values()[this.indexCategory]) {
                     modules.add(m);
                  }
               }

               ArrayList moduleMathHelper = new ArrayList(modules);
               moduleMathHelper.sort((o1, o2) -> {
                  return Client.INSTANCE.getFontManager().arial18.getStringWidth(o2.getName()) - Client.INSTANCE.getFontManager().arial18.getStringWidth(o1.getName());
               });
               int biggestModuleWidth = Client.INSTANCE.getFontManager().arial18.getStringWidth(((Module)moduleMathHelper.get(0)).getName()) + 15;

               for(Iterator var14 = modules.iterator(); var14.hasNext(); moduleYPos += 11.5F) {
                  Module m = (Module)var14.next();
                  RenderUtil.drawRect((double)y, (double)((float)posY + moduleYPos), (double)(y + biggestModuleWidth), (double)((float)posY + moduleYPos + 11.5F), (new Color(4, 4, 4, 180)).getRGB());
                  if (m == modules.get(this.indexModule)) {
                     RenderUtil.drawRect((double)y, (double)((float)posY + moduleYPos), (double)(y + biggestModuleWidth), (double)((float)posY + moduleYPos + 11.5F), staticColor);
                  }

                  Client.INSTANCE.getFontManager().arial18.drawStringWithShadow(m.getName(), (double)(y + 2), (double)((float)posY + moduleYPos + 3.0F), m.isEnabled() ? -1 : (new Color(180, 180, 180)).getRGB());
               }

               y += biggestModuleWidth + 1;
               if (this.indexValue != -1) {
                  ArrayList valueList = ((Module)modules.get(this.indexModule)).getValues();
                  ArrayList valueMathHealer = new ArrayList(valueList);
                  valueMathHealer.sort((o1, o2) -> {
                     return Client.INSTANCE.getFontManager().arial18.getStringWidth(o2.getName() + " §7" + o2.getValue()) - Client.INSTANCE.getFontManager().arial18.getStringWidth(o1.getName() + " §7" + o1.getValue());
                  });
                  roundedY = Client.INSTANCE.getFontManager().arial18.getStringWidth(((Value)valueMathHealer.get(0)).getName() + " §7" + ((Value)valueMathHealer.get(0)).getValue()) + 15;

                  for(Iterator var17 = valueList.iterator(); var17.hasNext(); valueYPos += 11.5F) {
                     Value value = (Value)var17.next();
                     RenderUtil.drawRect((double)y, (double)((float)posY + valueYPos), (double)(y + roundedY), (double)((float)posY + valueYPos + 11.5F), (new Color(4, 4, 4, 180)).getRGB());
                     if (value == ((Module)modules.get(this.indexModule)).getValues().get(this.indexValue)) {
                        RenderUtil.drawRect((double)y, (double)((float)posY + valueYPos), (double)(y + roundedY), (double)((float)posY + valueYPos + 11.5F), this.debuggingValue == value ? ColorUtil.getDarker(new Color(staticColor), 50, 255).getRGB() : staticColor);
                     }

                     Client.INSTANCE.getFontManager().arial18.drawStringWithShadow(value.getName() + " §7" + value.getValue(), (double)(y + 2), (double)((float)posY + valueYPos + 3.0F), -1);
                  }
               }
            }

            posY = (int)((float)posY + cateYPos);
            realCategoryY = (float)posY + cateYPos;
            float realModuleY = (float)posY + moduleYPos;
            float realValueY = (float)posY + valueYPos;
            if (realModuleY > realCategoryY) {
               posY = (int)((float)posY + (realModuleY - realCategoryY));
            }

            if (realValueY > realModuleY) {
               posY = (int)((float)posY + (realValueY - realModuleY));
            }

            posY += 3;
         }

         if ((Boolean)this.pPolylineValue.getValue()) {
            PPSRenderer incoming = new PPSRenderer(posX, posY, "In-Coming", Client.INSTANCE.getModuleManager().getNetWorkAssist().getInComingList());
            incoming.draw();
            PPSRenderer outgoing = new PPSRenderer(posX, posY + 40, "Out-Going", Client.INSTANCE.getModuleManager().getNetWorkAssist().getOutGoingList());
            outgoing.draw();
            posY += 80;
         }

         CFontRenderer font;
         if ((Boolean)this.activeModValue.getValue()) {
            font = Client.INSTANCE.getFontManager().arial18;
            ArrayList sort = new ArrayList(Client.INSTANCE.getModuleManager().getModuleList());
            sort.sort((o1, o2) -> {
               return Client.INSTANCE.getFontManager().arial18.getStringWidth(!o2.getLabel().equals("") ? o2.getName() + " §7" + o2.getLabel() : o2.getName()) - Client.INSTANCE.getFontManager().arial18.getStringWidth(!o1.getLabel().equals("") ? o1.getName() + " §7" + o1.getLabel() : o1.getName());
            });
            ArrayList lastModuleList = new ArrayList();
            Iterator var39 = sort.iterator();

            label405:
            while(true) {
               Module m;
               do {
                  do {
                     do {
                        if (!var39.hasNext()) {
                           y = 0;
                           Iterator var44 = sort.iterator();

                           while(true) {
                              Module m;
                              do {
                                 do {
                                    do {
                                       if (!var44.hasNext()) {
                                          break label405;
                                       }

                                       m = (Module)var44.next();
                                    } while(m.getAnimationX() == -1.0F && m.getAnimationY() == -1.0F);
                                 } while(m.isHidden());
                              } while(m.getCategory() == Category.Vision && m.getName() != "Xray");

                              String withLabel = m.getLabel().equals("") ? m.getName() : m.getName() + " §7" + m.getLabel() + "§r";
                              int color;
                              if (this.colorModeValue.getMode() == HUD.colorEnums.Random) {
                                 color = m.getRandomColor();
                              } else if (this.colorModeValue.getMode() == HUD.colorEnums.Category) {
                                 color = ColorUtil.getCategoryColor(m.getCategory());
                              } else if (this.colorModeValue.getMode() == HUD.colorEnums.Rainbow) {
                                 color = ColorUtil.getRainbow(((Number)secondValue.getValue()).floatValue(), ((Number)saturationValue.getValue()).floatValue(), ((Number)brightValue.getValue()).floatValue(), (long)(y / 11 * ((Number)differenceValue.getValue()).intValue()) * 100L).getRGB();
                              } else if (this.colorModeValue.getMode() == HUD.colorEnums.Fade) {
                                 color = ColorUtil.getFadeRainbow(new Color(((Number)redValue.getValue()).intValue(), ((Number)greenValue.getValue()).intValue(), ((Number)blueValue.getValue()).intValue()), y / 11, ((Number)differenceValue.getValue()).intValue()).getRGB();
                              } else {
                                 color = (new Color(((Number)redValue.getValue()).intValue(), ((Number)greenValue.getValue()).intValue(), ((Number)blueValue.getValue()).intValue())).getRGB();
                              }

                              if (m.isEnabled()) {
                                 if (m.getAnimationX() < (float)font.getStringWidth(withLabel)) {
                                    m.setAnimationX(m.getAnimationX() + (Minecraft.getDebugFPS() > 144 ? 1.0F : 2.5F));
                                 }

                                 if (m.getAnimationX() > (float)font.getStringWidth(withLabel)) {
                                    m.setAnimationX((float)font.getStringWidth(withLabel));
                                 }

                                 if (m.getAnimationY() < 11.0F) {
                                    m.setAnimationY(m.getAnimationY() + (Minecraft.getDebugFPS() > 144 ? 0.2F : 0.5F));
                                 }

                                 if (m.getAnimationY() > 11.0F) {
                                    m.setAnimationY(11.0F);
                                 }
                              } else {
                                 if (m.getAnimationX() > -1.0F) {
                                    m.setAnimationX(m.getAnimationX() - (Minecraft.getDebugFPS() > 144 ? 1.0F : 2.5F));
                                 }

                                 if (m.getAnimationY() > -1.0F) {
                                    m.setAnimationY(m.getAnimationY() - (Minecraft.getDebugFPS() > 144 ? 0.2F : 0.5F));
                                 }

                                 if (m.getAnimationX() < 0.0F) {
                                    m.setAnimationX(-1.0F);
                                 }

                                 if (m.getAnimationY() < 0.0F) {
                                    m.setAnimationY(-1.0F);
                                 }
                              }

                              int roundedX = Math.round(m.getAnimationX());
                              roundedY = Math.round(m.getAnimationY());
                              int LR = 0;
                              if ((Boolean)this.lRectValue.getValue()) {
                                 ++LR;
                              }

                              if ((Boolean)this.rRectValue.getValue()) {
                                 ++LR;
                              }

                              RenderUtil.startGlScissor(sr.getScaledWidth() - roundedX - 4 - LR, y + 11 - roundedY, font.getStringWidth(withLabel) + 4 + LR, (Boolean)this.bRectValue.getValue() ? 12 : 11);
                              RenderUtil.drawRect((double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 1 : 0)), (double)y, (double)(sr.getScaledWidth() - ((Boolean)this.rRectValue.getValue() ? 1 : 0)), (double)(y + 11), (new Color(0, 0, 0, ((Number)alphaValue.getValue()).intValue())).getRGB());
                              if ((Boolean)this.lRectValue.getValue()) {
                                 RenderUtil.drawRect((double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1)), (double)y, (double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 1 : 0)), (double)(y + 11), color);
                              }

                              if ((Boolean)this.rRectValue.getValue()) {
                                 RenderUtil.drawRect((double)(sr.getScaledWidth() - 1), (double)y, (double)sr.getScaledWidth(), (double)(y + 11), color);
                              }

                              if ((Boolean)this.bRectValue.getValue()) {
                                 int sWidth;
                                 int fWidth;
                                 if (lastModuleList.indexOf(m) == 0) {
                                    if (lastModuleList.size() > 1) {
                                       fWidth = font.getStringWidth(((Module)lastModuleList.get(0)).getLabel().equals("") ? ((Module)lastModuleList.get(0)).getName() : ((Module)lastModuleList.get(0)).getName() + " §7" + ((Module)lastModuleList.get(0)).getLabel() + "§r");
                                       sWidth = font.getStringWidth(((Module)lastModuleList.get(1)).getLabel().equals("") ? ((Module)lastModuleList.get(1)).getName() : ((Module)lastModuleList.get(1)).getName() + " §7" + ((Module)lastModuleList.get(1)).getLabel() + "§r");
                                       RenderUtil.drawRect((double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1)), (double)(y + 11), (double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1) + (fWidth - sWidth)), (double)(y + 12), color);
                                    } else {
                                       RenderUtil.drawRect((double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1)), (double)(y + 11), (double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1) + font.getStringWidth(withLabel) + 6), (double)(y + 12), color);
                                    }
                                 } else if (lastModuleList.indexOf(m) == lastModuleList.size() - 1) {
                                    RenderUtil.drawRect((double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1)), (double)(y + 11), (double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1) + font.getStringWidth(withLabel) + 6), (double)(y + 12), color);
                                 } else {
                                    fWidth = lastModuleList.indexOf(m);
                                    sWidth = fWidth + 1;
                                    int fWidth = font.getStringWidth(((Module)lastModuleList.get(fWidth)).getLabel().equals("") ? ((Module)lastModuleList.get(fWidth)).getName() : ((Module)lastModuleList.get(fWidth)).getName() + " §7" + ((Module)lastModuleList.get(fWidth)).getLabel() + "§r");
                                    int sWidth = font.getStringWidth(((Module)lastModuleList.get(sWidth)).getLabel().equals("") ? ((Module)lastModuleList.get(sWidth)).getName() : ((Module)lastModuleList.get(sWidth)).getName() + " §7" + ((Module)lastModuleList.get(sWidth)).getLabel() + "§r");
                                    RenderUtil.drawRect((double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1)), (double)(y + 11), (double)(sr.getScaledWidth() - font.getStringWidth(withLabel) - 4 - ((Boolean)this.rRectValue.getValue() ? 2 : 1) + (fWidth - sWidth)), (double)(y + 12), color);
                                 }
                              }

                              font.drawStringWithShadow(withLabel, (double)((float)(sr.getScaledWidth() - font.getStringWidth(withLabel)) - (2.5F + ((Boolean)this.rRectValue.getValue() ? 1.0F : 0.0F))), (double)((float)y + 2.5F), color);
                              RenderUtil.stopGlScissor();
                              y = (int)((float)y + Math.max(m.getAnimationY(), 0.0F));
                           }
                        }

                        m = (Module)var39.next();
                     } while(m.getAnimationX() == -1.0F && m.getAnimationY() == -1.0F);
                  } while(m.isHidden());
               } while(m.getCategory() == Category.Vision && m.getName() != "Xray");

               lastModuleList.add(m);
            }
         }

         if ((Boolean)this.compassValue.getValue()) {
            this.compass.draw(sr);
         }

         if ((Boolean)this.hotbarValue.getValue()) {
            RenderUtil.drawRect(0.0, (double)(sr.getScaledHeight() - 22), (double)((float)sr.getScaledWidth() / 2.0F - 90.0F), (double)sr.getScaledHeight(), (new Color(0, 0, 0, 150)).getRGB());
            RenderUtil.drawRect((double)((float)sr.getScaledWidth() / 2.0F + 90.0F), (double)(sr.getScaledHeight() - 22), (double)sr.getScaledWidth(), (double)sr.getScaledHeight(), (new Color(0, 0, 0, 150)).getRGB());
            font = Client.INSTANCE.getFontManager().arial16;
            CFontRenderer big = Client.INSTANCE.getFontManager().arial18;
            int color;
            if (this.colorModeValue.getMode() == HUD.colorEnums.Rainbow) {
               color = ColorUtil.getRainbow(((Number)secondValue.getValue()).floatValue(), ((Number)saturationValue.getValue()).floatValue(), ((Number)brightValue.getValue()).floatValue()).getRGB();
            } else if (this.colorModeValue.getMode() == HUD.colorEnums.Fade) {
               color = ColorUtil.getFadeRainbow(new Color(((Number)redValue.getValue()).intValue(), ((Number)greenValue.getValue()).intValue(), ((Number)blueValue.getValue()).intValue()), 1, 16).getRGB();
            } else {
               color = (new Color(((Number)redValue.getValue()).intValue(), ((Number)greenValue.getValue()).intValue(), ((Number)blueValue.getValue()).intValue())).getRGB();
            }

            font.drawStringWithShadow("POS: §f" + (int)mc.thePlayer.posX + " " + (int)mc.thePlayer.posY + " " + (int)mc.thePlayer.posZ, 2.0, (double)(sr.getScaledHeight() - 18), color);
            big.drawStringWithShadow("User: §f" + Client.USERNAME, 2.0, (double)(sr.getScaledHeight() - 9), color);
            font.drawStringWithShadow("Build: §fb" + Client.INSTANCE.getBuild(), (double)(sr.getScaledWidth() - font.getStringWidth("Build: §fb" + Client.INSTANCE.getBuild()) - 2), (double)(sr.getScaledHeight() - 18), color);
            big.drawStringWithShadow("Time: §f" + (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime()), (double)(sr.getScaledWidth() - big.getStringWidth("Time: §f" + (new SimpleDateFormat("HH:mm:ss")).format(Calendar.getInstance().getTime())) - 2), (double)(sr.getScaledHeight() - 9), color);
         }

         if ((Boolean)this.sortValue.getValue()) {
            int slot = 3;

            for(int xOffset = 0; slot >= 0; --slot) {
               ItemStack stack = mc.thePlayer.inventory.armorItemInSlot(slot);
               if (stack != null) {
                  mc.getRenderItem().renderItemIntoGUI(stack, sr.getScaledWidth() / 2 + 15 - xOffset, sr.getScaledHeight() - 55 - (mc.thePlayer.isInsideOfMaterial(Material.water) && mc.thePlayer.getAir() > 0 ? 10 : 0));
                  xOffset -= 18;
               }
            }
         }

      }
   }

   @EventTarget
   void onKey(KeyTypedEvent event) {
      int key = event.getKey();
      int down = 208;
      int up = 200;
      int left = 203;
      int right = 205;
      int enter = 28;
      BoolValue boolValue;
      double inc;
      double min;
      double max;
      double target;
      double afterMath;
      NumValue numValue;
      EnumValue enumValue;
      ArrayList modules;
      Enum current;
      Module m;
      Iterator var24;
      if (key == down) {
         if (this.debuggingValue != null) {
            if (this.debuggingValue instanceof BoolValue) {
               boolValue = (BoolValue)this.debuggingValue;
               boolValue.setValue(!(Boolean)boolValue.getValue());
            } else if (this.debuggingValue instanceof NumValue) {
               numValue = (NumValue)this.debuggingValue;
               inc = numValue.getIncrement().doubleValue();
               min = numValue.getMinimum().doubleValue();
               max = numValue.getMaximum().doubleValue();
               target = Double.parseDouble(String.valueOf(((Number)numValue.getValue()).doubleValue() - inc));
               afterMath = (double)Math.round(Math.max(min, Math.min(max, target)) * (1.0 / inc)) / (1.0 / inc);
               numValue.setValue(afterMath);
            } else if (this.debuggingValue instanceof EnumValue) {
               enumValue = (EnumValue)this.debuggingValue;
               current = (Enum)enumValue.getValue();
               int next = current.ordinal() + 1 >= enumValue.getModes().length ? 0 : current.ordinal() + 1;
               enumValue.setValue(enumValue.getModes()[next]);
            }
         } else if (this.indexValue >= 0) {
            ++this.indexValue;
            modules = new ArrayList();
            var24 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(var24.hasNext()) {
               m = (Module)var24.next();
               if (m.getCategory() == Category.values()[this.indexCategory]) {
                  modules.add(m);
               }
            }

            m = (Module)modules.get(this.indexModule);
            if (this.indexValue >= m.getValues().size()) {
               this.indexValue = 0;
            }
         } else if (this.indexModule >= 0) {
            ++this.indexModule;
            modules = new ArrayList();
            var24 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(var24.hasNext()) {
               m = (Module)var24.next();
               if (m.getCategory() == Category.values()[this.indexCategory]) {
                  modules.add(m);
               }
            }

            if (this.indexModule >= modules.size()) {
               this.indexModule = 0;
            }
         } else {
            ++this.indexCategory;
            if (this.indexCategory >= Category.values().length) {
               this.indexCategory = 0;
            }
         }
      } else if (key == up) {
         if (this.debuggingValue != null) {
            if (this.debuggingValue instanceof BoolValue) {
               boolValue = (BoolValue)this.debuggingValue;
               boolValue.setValue(!(Boolean)boolValue.getValue());
            } else if (this.debuggingValue instanceof NumValue) {
               numValue = (NumValue)this.debuggingValue;
               inc = numValue.getIncrement().doubleValue();
               min = numValue.getMinimum().doubleValue();
               max = numValue.getMaximum().doubleValue();
               target = Double.parseDouble(String.valueOf(((Number)numValue.getValue()).doubleValue() + inc));
               afterMath = (double)Math.round(Math.max(min, Math.min(max, target)) * (1.0 / inc)) / (1.0 / inc);
               numValue.setValue(afterMath);
            } else if (this.debuggingValue instanceof EnumValue) {
               enumValue = (EnumValue)this.debuggingValue;
               current = (Enum)enumValue.getValue();
               enumValue.setValue(enumValue.getModes()[current.ordinal() - 1 < 0 ? enumValue.getModes().length - 1 : current.ordinal() - 1]);
            }
         } else if (this.indexValue >= 0) {
            modules = new ArrayList();
            var24 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(var24.hasNext()) {
               m = (Module)var24.next();
               if (m.getCategory() == Category.values()[this.indexCategory]) {
                  modules.add(m);
               }
            }

            m = (Module)modules.get(this.indexModule);
            if (this.indexValue - 1 != -1) {
               --this.indexValue;
            } else {
               this.indexValue = m.getValues().size() - 1;
            }
         } else if (this.indexModule >= 0) {
            modules = new ArrayList();
            var24 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(var24.hasNext()) {
               m = (Module)var24.next();
               if (m.getCategory() == Category.values()[this.indexCategory]) {
                  modules.add(m);
               }
            }

            if (this.indexModule - 1 != -1) {
               --this.indexModule;
            } else {
               this.indexModule = modules.size() - 1;
            }
         } else {
            --this.indexCategory;
            if (this.indexCategory < 0) {
               this.indexCategory = Category.values().length - 1;
            }
         }
      } else if (key == right) {
         if (this.debuggingValue != null) {
            return;
         }

         if (this.indexModule == -1) {
            this.indexModule = 0;
         } else if (this.indexValue == -1) {
            modules = new ArrayList();
            var24 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(var24.hasNext()) {
               m = (Module)var24.next();
               if (m.getCategory() == Category.values()[this.indexCategory]) {
                  modules.add(m);
               }
            }

            m = (Module)modules.get(this.indexModule);
            if (!m.getValues().isEmpty()) {
               this.indexValue = 0;
            }
         }
      } else if (key == left) {
         if (this.debuggingValue != null) {
            return;
         }

         if (this.indexValue != -1) {
            this.indexValue = -1;
         } else if (this.indexModule != -1) {
            this.indexModule = -1;
         }
      } else if (key == enter && this.indexModule != -1 && this.indexValue != -1) {
         if (this.debuggingValue == null) {
            modules = new ArrayList();
            var24 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(var24.hasNext()) {
               m = (Module)var24.next();
               if (m.getCategory() == Category.values()[this.indexCategory]) {
                  modules.add(m);
               }
            }

            m = (Module)modules.get(this.indexModule);
            this.debuggingValue = (Value)m.getValues().get(this.indexValue);
         } else {
            this.debuggingValue = null;
         }
      }

   }

   public static Color getCustomColor() {
      return new Color(((Number)redValue.getValue()).intValue(), ((Number)greenValue.getValue()).intValue(), ((Number)blueValue.getValue()).intValue());
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$vision$HUD$markEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$vision$HUD$markEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[HUD.markEnums.values().length];

         try {
            var0[HUD.markEnums.Animated.ordinal()] = 3;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[HUD.markEnums.CSGO.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[HUD.markEnums.Normal.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[HUD.markEnums.Simple.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[HUD.markEnums.Small.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$vision$HUD$markEnums = var0;
         return var0;
      }
   }

   static class PPSRenderer {
      private int x;
      private int y;
      private final String fuckingName;
      private final ArrayList list;

      public PPSRenderer(int xx, int yy, String name, ArrayList targetList) {
         this.x = xx;
         this.y = yy;
         this.list = targetList;
         this.fuckingName = name;
      }

      public void draw() {
         int renderY = Math.max(Math.round((float)(this.y + 30) - (float)(Integer)this.list.get(this.list.size() - 1) / 26.0F - 2.0F), this.y);
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5, 0.5, 0.5);
         HUD.mc.fontRendererObj.drawStringWithShadow(this.fuckingName + " Packet Count/s", (float)((this.x + 1) * 2), (float)(this.y * 2), -1);
         HUD.mc.fontRendererObj.drawStringWithShadow(this.list.get(this.list.size() - 1) + " P/s", (float)((this.x + 100) * 2), (float)(renderY * 2), -1);
         GlStateManager.popMatrix();
         this.y += 5;
         RenderUtil.drawBordRect((double)this.x, (double)this.y, (double)(this.x + 95), (double)(this.y + 30), 0.5, (new Color(0, 0, 0, 150)).getRGB(), -1);
         RenderUtil.startGlScissor(this.x, this.y, 95, 30);
         int xOffset = this.x;

         for(int i = 0; i < this.list.size(); ++i) {
            if (this.list.size() > 1 && i > 1) {
               this.connectPoints((float)xOffset - 3.5F, (float)this.y + 29.5F - (float)(Integer)this.list.get(i - 1) / 26.0F, (float)xOffset - 1.5F, (float)this.y + 29.5F - (float)(Integer)this.list.get(i) / 26.0F, -1);
            }

            xOffset += 2;
            if (xOffset == 103) {
               this.list.remove(0);
            }
         }

         RenderUtil.drawRect((double)this.x, (double)(Math.round((float)(this.y + 30) - (float)(Integer)this.list.get(this.list.size() - 1) / 26.0F - 2.0F) + 1), (double)(this.x + 95), (double)((float)Math.round((float)(this.y + 30) - (float)(Integer)this.list.get(this.list.size() - 1) / 26.0F - 2.0F) + 1.5F), Color.GREEN.getRGB());
         RenderUtil.stopGlScissor();
      }

      private void connectPoints(float xOne, float yOne, float xTwo, float yTwo, int color) {
         GL11.glPushMatrix();
         GL11.glEnable(2848);
         ColorUtil.glColor(color);
         GL11.glDisable(3553);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(3042);
         GL11.glLineWidth(2.0F);
         GL11.glBegin(1);
         GL11.glVertex2f(xOne, yOne);
         GL11.glVertex2f(xTwo, yTwo);
         GL11.glEnd();
         GlStateManager.resetColor();
         GL11.glDisable(2848);
         GL11.glEnable(3553);
         GL11.glPopMatrix();
      }
   }

   private static enum colorEnums {
      Custom,
      Fade,
      Rainbow,
      Category,
      Random;
   }

   private static enum markEnums {
      Normal,
      Simple,
      Animated,
      Small,
      CSGO;
   }
}
