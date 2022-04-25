package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.Iterator;
import me.bigg.event.OverlayEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.combat.AntiBot;
import me.bigg.util.ColorUtil;
import me.bigg.util.RenderUtil;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class Arrow extends Module {
   private final NumValue redValue = new NumValue("Red", "Red color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue greenValue = new NumValue("Green", "Green color", 255.0, 0.0, 255.0, 5.0);
   private final NumValue blueValue = new NumValue("Blue", "Blue color", 255.0, 0.0, 255.0, 5.0);
   private final EnumValue colorModeValue = new EnumValue("Color Mode", "Just color mode", Arrow.colorEnums.values());
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$vision$Arrow$colorEnums;

   public Arrow() {
      super("Arrow", "Fuck", Category.Vision);
      this.addValues(new Value[]{this.colorModeValue, this.redValue, this.greenValue, this.blueValue});
   }

   @EventTarget
   void onOverlay(OverlayEvent event) {
      ScaledResolution sr = event.getScaledResolution();
      int width = sr.getScaledWidth();
      int height = sr.getScaledHeight();
      Iterator var6 = mc.theWorld.playerEntities.iterator();

      while(var6.hasNext()) {
         EntityPlayer player = (EntityPlayer)var6.next();
         if (player != mc.thePlayer && !AntiBot.isBot(player)) {
            double mineX = mc.thePlayer.posX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * (double)mc.timer.renderPartialTicks;
            double mineZ = mc.thePlayer.posZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * (double)mc.timer.renderPartialTicks;
            double targetX = player.posX + (player.posX - player.lastTickPosX) * (double)mc.timer.renderPartialTicks;
            double targetZ = player.posZ + (player.posZ - player.lastTickPosZ) * (double)mc.timer.renderPartialTicks;
            double xDiff = targetX - mineX;
            double zDiff = targetZ - mineZ;
            double cos = Math.cos((double)mc.thePlayer.rotationYaw * 0.017453292519943295);
            double sin = Math.sin((double)mc.thePlayer.rotationYaw * 0.017453292519943295);
            double rotY = -(zDiff * cos - xDiff * sin);
            double rotX = -(xDiff * cos + zDiff * sin);
            float angle = (float)(Math.atan2(rotY, rotX) * 180.0 / Math.PI);
            double x = 25.0 * Math.cos(Math.toRadians((double)angle)) + (double)width / 2.0;
            double y = 25.0 * Math.sin(Math.toRadians((double)angle)) + (double)height / 2.0;
            Color color = this.getColor();
            switch ($SWITCH_TABLE$me$bigg$module$impl$vision$Arrow$colorEnums()[((colorEnums)this.colorModeValue.getMode()).ordinal()]) {
               case 2:
                  Color rain = ColorUtil.getRainbow(10.0F, 1.0F, 1.0F);
                  color = new Color(rain.getRed(), rain.getGreen(), rain.getBlue());
                  break;
               case 3:
                  Color blend = ColorUtil.getBlendColor((double)player.getHealth(), (double)player.getMaxHealth());
                  color = new Color(blend.getRed(), blend.getGreen(), blend.getBlue());
                  break;
               case 4:
                  String text = player.getDisplayName().getFormattedText();
                  if (Character.toLowerCase(text.charAt(0)) == 25602) {
                     char oneMore = Character.toLowerCase(text.charAt(1));
                     int colorCode = "0123456789abcdefklmnorg".indexOf(oneMore);
                     if (colorCode < 16) {
                        try {
                           int newColor = mc.fontRendererObj.colorCode[colorCode];
                           color = new Color(newColor >> 16, newColor >> 8 & 255, newColor & 255);
                        } catch (ArrayIndexOutOfBoundsException var39) {
                        }
                     }
                  } else {
                     color = new Color(255, 255, 255);
                  }
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0.0);
            RenderUtil.drawCircle(0.0F, 0.0F, 3.0F, (new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min((int)Math.abs(mc.thePlayer.getDistanceToEntity(player) * 5.0F), 255))).getRGB());
            GlStateManager.popMatrix();
         }
      }

   }

   public Color getColor() {
      return new Color(((Number)this.redValue.getValue()).intValue(), ((Number)this.greenValue.getValue()).intValue(), ((Number)this.blueValue.getValue()).intValue());
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$vision$Arrow$colorEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$vision$Arrow$colorEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[Arrow.colorEnums.values().length];

         try {
            var0[Arrow.colorEnums.Custom.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[Arrow.colorEnums.Health.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[Arrow.colorEnums.Rainbow.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[Arrow.colorEnums.Team.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$vision$Arrow$colorEnums = var0;
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
