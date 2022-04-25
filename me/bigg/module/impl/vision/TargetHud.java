package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import me.bigg.Client;
import me.bigg.event.OverlayEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.combat.KillAura;
import me.bigg.util.ColorUtil;
import me.bigg.util.MathUtil;
import me.bigg.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class TargetHud extends Module {
   private static int hudX = 30;
   private static int hudY = 30;

   public TargetHud() {
      super("Target Hud", "Display gui", Category.Vision);
   }

   public void onEnable() {
      super.onEnable();
   }

   @EventTarget
   void onOverlay(OverlayEvent event) {
      int width = event.getScaledResolution().getScaledWidth();
      int height = event.getScaledResolution().getScaledHeight();
      EntityLivingBase target = mc.currentScreen instanceof GuiChat ? mc.thePlayer : (KillAura.getTarget() != null ? KillAura.getTarget() : (KillAura.getBlockTarget() != null ? KillAura.getBlockTarget() : null));
      if (target != null) {
         Gui.drawRect(hudX, hudY + 20, hudX + 110, hudY + 60, Color.BLACK.getRGB());
         Gui.drawRect(hudX + 1, hudY + 20 + 1, hudX + 110 - 1, hudY + 60 - 1, (new Color(55, 55, 55)).getRGB());
         RenderUtil.drawRect((double)(hudX + 1), (double)hudY + 21.5, (double)(hudX + 108), (double)hudY + 58.5, (new Color(30, 30, 30)).getRGB());
         Gui.drawRect(hudX + 3, hudY + 20 + 3, hudX + 110 - 3, hudY + 60 - 3, (new Color(14, 14, 14)).getRGB());
         int x = hudX + 3;
         int y = hudY + 20 + 3;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GuiInventory.drawEntityOnScreen(x + 15, y + 33, 15, ((EntityLivingBase)target).rotationYaw, ((EntityLivingBase)target).rotationPitch, (EntityLivingBase)target);
         GlStateManager.resetColor();
         x += 30;
         Client.INSTANCE.getFontManager().arial14.drawStringWithShadow("§l" + ((EntityLivingBase)target).getName(), (double)(x + 1), (double)(y + 5), -1);
         RenderUtil.drawBordRect((double)x, (double)(y + 12), (double)(x + 55), (double)(y + 16), 0.5, (new Color(40, 40, 40)).getRGB(), Color.BLACK.getRGB());
         double hpWidth = 55.0 * MathHelper.clamp_double((double)(((EntityLivingBase)target).getHealth() / ((EntityLivingBase)target).getMaxHealth()), 0.0, 1.0);
         RenderUtil.drawRect((double)((float)x + 0.5F), (double)((float)(y + 11) + 0.5F), (double)x + hpWidth - 0.5, (double)(y + 15), ColorUtil.getBlendColor((double)((EntityLivingBase)target).getHealth(), (double)((EntityLivingBase)target).getMaxHealth()).getRGB());
         int xOff = 6;

         for(int i = 0; i < 8; ++i) {
            RenderUtil.drawRect((double)(x + xOff), (double)(y + 11), (double)((float)(x + xOff) + 0.5F), (double)(y + 15), Color.BLACK.getRGB());
            xOff += 6;
         }

         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5, 0.5, 0.5);
         mc.fontRendererObj.drawStringWithShadow("HP:" + (int)((EntityLivingBase)target).getHealth() + " | Dist:" + (int)mc.thePlayer.getDistanceToEntity((Entity)target), (float)((x + 1) * 2), (float)((y + 17) * 2), -1);
         GlStateManager.popMatrix();
         if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)target;
            int xOffset = 3;

            for(int slot = 3; slot >= 0; --slot) {
               ItemStack stack = player.inventory.armorItemInSlot(slot);
               if (stack != null) {
                  mc.getRenderItem().renderItemIntoGUI(stack, x - xOffset, y + 20);
                  xOffset -= 15;
               }
            }

            ItemStack stack = player.inventory.getCurrentItem();
            if (stack != null) {
               RenderHelper.enableGUIStandardItemLighting();
               mc.getRenderItem().renderItemIntoGUI(stack, x - xOffset, y + 18);
               RenderHelper.disableStandardItemLighting();
            }
         }
      }

   }

   public static int getHudX() {
      return hudX;
   }

   public static int getHudY() {
      return hudY;
   }

   public static void setHudX(int hudX) {
      TargetHud.hudX = hudX;
   }

   public static void setHudY(int hudY) {
      TargetHud.hudY = hudY;
   }

   public static boolean isHover(int mouseX, int mouseY) {
      return MathUtil.inRange((double)mouseX, (double)mouseY, (double)(hudX + 110), (double)(hudY + 60), (double)hudX, (double)(hudY + 20)) && Client.INSTANCE.getModuleManager().getModule("targethud").isEnabled();
   }
}
