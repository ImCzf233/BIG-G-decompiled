package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.Iterator;
import me.bigg.event.RenderEvent;
import me.bigg.event.RenderNameEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.combat.AntiBot;
import me.bigg.module.impl.misc.HackerDetect;
import me.bigg.util.RenderUtil;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class NameTag extends Module {
   private final NumValue scaleValue = new NumValue("NameTags", "Scale", 1.0, 0.1, 1.0, 0.1);

   public NameTag() {
      super("Name Tags", "Render name tags", Category.Vision);
      this.addValues(new Value[]{this.scaleValue});
   }

   @EventTarget
   private void onRenderNameTag(RenderNameEvent event) {
      if (event.getEntity() instanceof EntityPlayer && !AntiBot.isBot((EntityLivingBase)event.getEntity())) {
         event.setCancelled(true);
      }

   }

   @EventTarget(4)
   private void onRender3DEvent(RenderEvent event) {
      if (mc.theWorld != null && mc.thePlayer != null && !mc.theWorld.getLoadedEntityList().isEmpty() && !mc.theWorld.playerEntities.isEmpty()) {
         Iterator var3 = mc.theWorld.playerEntities.iterator();

         while(var3.hasNext()) {
            EntityPlayer entity = (EntityPlayer)var3.next();
            if (!AntiBot.isBot(entity) && entity != mc.thePlayer) {
               double yOffset = entity.isSneaking() ? -0.25 : 0.0;
               double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
               double posY = entity.lastTickPosY + yOffset + (entity.posY + yOffset - (entity.lastTickPosY + yOffset)) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
               double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
               mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
               this.renderNameTag(entity, posX, posY, posZ, event.getPartialTicks());
            }
         }

      }
   }

   private double interpolate(double previous, double current, float delta) {
      return previous + (current - previous) * (double)delta;
   }

   private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
      double tempY = y + 0.7;
      Entity camera = mc.getRenderViewEntity();
      double originalPositionX = camera.posX;
      double originalPositionY = camera.posY;
      double originalPositionZ = camera.posZ;
      camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
      camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
      camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
      double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
      float width = (float)mc.fontRendererObj.getStringWidth(this.getDisplayName(player)) / 2.0F;
      double scale = 0.004 * ((Number)this.scaleValue.getValue()).doubleValue() * distance;
      if (scale < 0.01) {
         scale = 0.01;
      }

      GlStateManager.pushMatrix();
      GlStateManager.enablePolygonOffset();
      GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
      GlStateManager.disableLighting();
      GlStateManager.translate((float)x, (float)tempY + 1.4F, (float)z);
      GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
      float var10001 = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
      GlStateManager.rotate(mc.getRenderManager().playerViewX, var10001, 0.0F, 0.0F);
      GlStateManager.scale(-scale, -scale, scale);
      drawRect3D(-width - 2.0F, -12.0F, width + 2.0F, 0.0F, (float)scale, (new Color(HackerDetect.isHacker(player) ? 255 : 0, 0, 0, 200)).getRGB(), (new Color(0, 0, 0, 180)).getRGB());
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDepthMask(false);
      mc.fontRendererObj.drawStringWithShadow(this.getDisplayName(player), -width, -9.5F, this.getDisplayColour(player));
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDepthMask(true);
      GlStateManager.disableLighting();
      camera.posX = originalPositionX;
      camera.posY = originalPositionY;
      camera.posZ = originalPositionZ;
      GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.popMatrix();
   }

   private int getDisplayColour(EntityPlayer player) {
      int colour = (new Color(16777215)).getRGB();
      if (player.isInvisible()) {
         colour = -1113785;
      }

      return colour;
   }

   private String getDisplayName(EntityLivingBase entity) {
      String drawTag = entity.getDisplayName().getFormattedText();
      EnumChatFormatting color;
      if ((double)((int)entity.getHealth()) >= 6.0) {
         color = EnumChatFormatting.GREEN;
      } else if ((double)((int)entity.getHealth()) >= 2.0) {
         color = EnumChatFormatting.YELLOW;
      } else {
         color = EnumChatFormatting.RED;
      }

      drawTag = EnumChatFormatting.GRAY + "[" + (int)entity.getDistanceToEntity(mc.thePlayer) + "m] " + EnumChatFormatting.RESET + drawTag + " " + color + (int)entity.getHealth();
      return drawTag;
   }

   public static void drawRect3D(float x, float y, float x1, float y1, float lineWidth, int inside, int border) {
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glDepthMask(true);
      GL11.glEnable(2848);
      GL11.glHint(3154, 4354);
      GL11.glHint(3155, 4354);
      RenderUtil.drawRect((double)x, (double)y, (double)x1, (double)y1, inside);
      float alpha = (float)(border >> 24 & 255) / 255.0F;
      float red = (float)(border >> 16 & 255) / 255.0F;
      float green = (float)(border >> 8 & 255) / 255.0F;
      float blue = (float)(border & 255) / 255.0F;
      GL11.glColor4f(red, green, blue, alpha);
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(lineWidth);
      GL11.glBegin(3);
      GL11.glVertex2f(x, y);
      GL11.glVertex2f(x, y1);
      GL11.glVertex2f(x1, y1);
      GL11.glVertex2f(x1, y);
      GL11.glVertex2f(x, y);
      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      GL11.glEnable(2929);
      GL11.glDisable(2848);
      GL11.glHint(3154, 4352);
      GL11.glHint(3155, 4352);
   }
}
