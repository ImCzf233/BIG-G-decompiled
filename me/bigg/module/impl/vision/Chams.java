package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import me.bigg.event.RLEEvent;
import me.bigg.event.RenderArmEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.ColorUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
   private final TimerUtil pulseTimer = new TimerUtil();
   private boolean pulsing;
   private float nope;
   private final BoolValue coloredValue = new BoolValue("Colored", "Colored ent", true);
   private final BoolValue pulseValue = new BoolValue("Pulse", "CSGO Be like", false);
   private final BoolValue handValue = new BoolValue("Hand", "CSGO Be like", true);
   private final BoolValue fillValue = new BoolValue("Fill", "Colored fill", true);
   private final NumValue redValue = new NumValue("Red", "Red color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue greenValue = new NumValue("Green", "Green color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue blueValue = new NumValue("Blue", "Blue color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue alphaValue = new NumValue("Alpha", "Alpha color", 35.0, 0.0, 255.0, 5.0);
   private final EnumValue colorModeValue = new EnumValue("Color Mode", "Just chams color mode", Chams.colorEnums.values());
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$vision$Chams$colorEnums;

   public Chams() {
      super("Chams", "Wall hacks", Category.Vision);
      this.addValues(new Value[]{this.colorModeValue, this.coloredValue, this.pulseValue, this.handValue, this.fillValue, this.redValue, this.greenValue, this.blueValue, this.alphaValue});
   }

   public void onEnable() {
      super.onEnable();
      this.pulseTimer.reset();
      this.pulsing = false;
      this.nope = 0.0F;
   }

   @EventTarget
   void onRenderModel(RenderArmEvent event) {
      if ((Boolean)this.handValue.getValue()) {
         try {
            if (event.getEntity() == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) {
               if (event.isPre()) {
                  GlStateManager.enableBlend();
                  GlStateManager.disableDepth();
                  GlStateManager.disableLighting();
                  GlStateManager.disableTexture2D();
                  mc.entityRenderer.disableLightmap();
                  Color color = this.getColor();
                  switch ($SWITCH_TABLE$me$bigg$module$impl$vision$Chams$colorEnums()[((colorEnums)this.colorModeValue.getMode()).ordinal()]) {
                     case 2:
                        Color rain = ColorUtil.getRainbow(10.0F, 1.0F, 1.0F);
                        color = new Color(rain.getRed(), rain.getGreen(), rain.getBlue(), ((Number)this.alphaValue.getValue()).intValue());
                        break;
                     case 3:
                        Color blend = ColorUtil.getBlendColor((double)mc.thePlayer.getHealth(), (double)mc.thePlayer.getMaxHealth());
                        color = new Color(blend.getRed(), blend.getGreen(), blend.getBlue(), ((Number)this.alphaValue.getValue()).intValue());
                        break;
                     case 4:
                        String text = mc.thePlayer.getDisplayName().getFormattedText();
                        if (Character.toLowerCase(text.charAt(0)) == 25602) {
                           char oneMore = Character.toLowerCase(text.charAt(1));
                           int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
                           if (colorCode < 16) {
                              try {
                                 int newColor = mc.fontRendererObj.colorCode[colorCode];
                                 color = new Color(newColor >> 16, newColor >> 8 & 255, newColor & 255, ((Number)this.alphaValue.getValue()).intValue());
                              } catch (ArrayIndexOutOfBoundsException var9) {
                              }
                           }
                        } else {
                           color = new Color(255, 255, 255, ((Number)this.alphaValue.getValue()).intValue());
                        }
                  }

                  GlStateManager.color(0.003921569F * (float)color.getRed(), 0.003921569F * (float)color.getGreen(), 0.003921569F * (float)color.getBlue(), 0.003921569F * (float)color.getAlpha());
               }

               if (event.isPost()) {
                  GlStateManager.resetColor();
                  mc.entityRenderer.enableLightmap();
                  GlStateManager.enableLighting();
                  GlStateManager.enableDepth();
                  GlStateManager.disableBlend();
                  GlStateManager.enableTexture2D();
               }
            }
         } catch (Exception var10) {
         }

      }
   }

   @EventTarget
   void onRLE(RLEEvent e) {
      if (e.getEntity() instanceof EntityPlayer && e.isPre()) {
         if ((Boolean)this.coloredValue.getValue()) {
            e.setCancelled(true);

            try {
               Render render = mc.getRenderManager().getEntityRenderObject(e.getEntity());
               if (render != null && mc.getRenderManager().renderEngine != null && render instanceof RendererLivingEntity) {
                  RendererLivingEntity rendererLivingEntity = (RendererLivingEntity)render;
                  GlStateManager.pushMatrix();
                  GlStateManager.disableTexture2D();
                  GlStateManager.disableAlpha();
                  mc.entityRenderer.disableLightmap();
                  GlStateManager.enableBlend();
                  if ((Boolean)this.fillValue.getValue()) {
                     GlStateManager.disableLighting();
                  }

                  Color color = this.getColor();
                  switch ($SWITCH_TABLE$me$bigg$module$impl$vision$Chams$colorEnums()[((colorEnums)this.colorModeValue.getMode()).ordinal()]) {
                     case 2:
                        Color rain = ColorUtil.getRainbow(10.0F, 1.0F, 1.0F);
                        color = new Color(rain.getRed(), rain.getGreen(), rain.getBlue(), ((Number)this.alphaValue.getValue()).intValue());
                        break;
                     case 3:
                        Color blend = ColorUtil.getBlendColor((double)e.getEntity().getHealth(), (double)e.getEntity().getMaxHealth());
                        color = new Color(blend.getRed(), blend.getGreen(), blend.getBlue(), ((Number)this.alphaValue.getValue()).intValue());
                        break;
                     case 4:
                        String text = e.getEntity().getDisplayName().getFormattedText();
                        if (Character.toLowerCase(text.charAt(0)) == 25602) {
                           char oneMore = Character.toLowerCase(text.charAt(1));
                           int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
                           if (colorCode < 16) {
                              try {
                                 int newColor = mc.fontRendererObj.colorCode[colorCode];
                                 color = new Color(newColor >> 16, newColor >> 8 & 255, newColor & 255, ((Number)this.alphaValue.getValue()).intValue());
                              } catch (ArrayIndexOutOfBoundsException var11) {
                              }
                           }
                        } else {
                           color = new Color(255, 255, 255, ((Number)this.alphaValue.getValue()).intValue());
                        }
                  }

                  if ((Boolean)this.pulseValue.getValue() && this.pulseTimer.hasPassed(15L)) {
                     if (this.pulsing && this.nope >= 0.5F) {
                        this.pulsing = false;
                     }

                     if (!this.pulsing && this.nope <= 0.0F) {
                        this.pulsing = true;
                     }

                     if (this.pulsing) {
                        this.nope += 0.02F;
                     } else {
                        this.nope -= 0.015F;
                     }

                     if (this.nope > 1.0F) {
                        this.nope = 1.0F;
                     } else if (this.nope < 0.0F) {
                        this.nope = 0.0F;
                     }

                     this.pulseTimer.reset();
                  }

                  GlStateManager.color(0.003921569F * (float)color.getRed(), 0.003921569F * (float)color.getGreen(), 0.003921569F * (float)color.getBlue(), (Boolean)this.pulseValue.getValue() ? this.nope : 0.003921569F * (float)color.getAlpha());
                  GlStateManager.disableDepth();
                  rendererLivingEntity.renderModel(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getAgeInTicks(), e.getRotationYawHead(), e.getRotationPitch(), e.getOffset());
                  GlStateManager.enableDepth();
                  rendererLivingEntity.renderModel(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getAgeInTicks(), e.getRotationYawHead(), e.getRotationPitch(), e.getOffset());
                  GlStateManager.resetColor();
                  if ((Boolean)this.fillValue.getValue()) {
                     GlStateManager.enableLighting();
                  }

                  GlStateManager.disableBlend();
                  mc.entityRenderer.enableLightmap();
                  GlStateManager.enableAlpha();
                  GlStateManager.enableTexture2D();
                  GlStateManager.popMatrix();
                  rendererLivingEntity.renderLayers(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getTick(), e.getAgeInTicks(), e.getRotationYawHead(), e.getRotationPitch(), e.getOffset());
                  GlStateManager.popMatrix();
               }
            } catch (Exception var12) {
            }
         } else {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0F, -1100000.0F);
         }
      } else if (!(Boolean)this.coloredValue.getValue() && e.getEntity() instanceof EntityPlayer && e.isPost()) {
         GL11.glDisable(32823);
         GL11.glPolygonOffset(1.0F, 1100000.0F);
      }

   }

   public Color getColor() {
      return new Color(((Number)this.redValue.getValue()).intValue(), ((Number)this.greenValue.getValue()).intValue(), ((Number)this.blueValue.getValue()).intValue(), ((Number)this.alphaValue.getValue()).intValue());
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$vision$Chams$colorEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$vision$Chams$colorEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Chams.colorEnums.values().length];

         try {
            var0[Chams.colorEnums.Custom.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Chams.colorEnums.Health.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Chams.colorEnums.Rainbow.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Chams.colorEnums.Team.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$vision$Chams$colorEnums = var0;
         return var0;
      }
   }

   private static enum colorEnums {
      Custom,
      Rainbow,
      Health,
      Team;
   }
}
