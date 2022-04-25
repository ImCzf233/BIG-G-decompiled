package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.bigg.event.LivingUpdateEvent;
import me.bigg.event.RenderEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.sub.Location;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class DMGParticle extends Module {
   private final HashMap healthMap = new HashMap();
   private final List particles = new ArrayList();

   public DMGParticle() {
      super("DMG Particle", "How many you damged entity", Category.Vision);
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.thePlayer.ticksExisted <= 1) {
         this.particles.clear();
         this.healthMap.clear();
      }

      if (!this.particles.isEmpty()) {
         this.particles.forEach((particle) -> {
            ++particle.ticks;
            if (particle.ticks <= 20) {
               particle.location.setY(particle.location.getY() + (double)particle.ticks * 0.001);
               particle.scale -= (float)particle.ticks * 0.01F;
            }

            if (particle.ticks > 20) {
               this.particles.remove(particle);
            }

         });
      }

   }

   @EventTarget
   public void onLivingUpdate(LivingUpdateEvent event) {
      EntityLivingBase entity = event.getEntity();
      if (entity != mc.thePlayer) {
         if (!this.healthMap.containsKey(entity)) {
            this.healthMap.put(entity, entity.getHealth());
         }

         float before = (Float)this.healthMap.get(entity);
         float after = entity.getHealth();
         if (before != after) {
            String text;
            if (before - after < 0.0F) {
               text = "§a" + Math.round(before - after) * -1;
            } else {
               text = "§c" + Math.round(before - after);
            }

            Location location = new Location(entity);
            location.setY(entity.getEntityBoundingBox().minY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0);
            location.setX(location.getX());
            location.setZ(location.getZ());
            this.particles.add(new Particle(location, text));
            this.healthMap.remove(entity);
            this.healthMap.put(entity, entity.getHealth());
         }

      }
   }

   @EventTarget
   public void onRenderWorld(RenderEvent event) {
      Iterator var3 = this.particles.iterator();

      while(var3.hasNext()) {
         Particle particle = (Particle)var3.next();
         double x = particle.location.getX() - mc.getRenderManager().renderPosX;
         double y = particle.location.getY() - mc.getRenderManager().renderPosY;
         double z = particle.location.getZ() - mc.getRenderManager().renderPosZ;
         GlStateManager.pushMatrix();
         GlStateManager.enablePolygonOffset();
         GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
         GlStateManager.translate((float)x, (float)y, (float)z);
         GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
         float var10001 = mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;
         GlStateManager.rotate(mc.getRenderManager().playerViewX, var10001, 0.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(-0.03, -0.03, 0.03);
         GlStateManager.scale(particle.scale, particle.scale, particle.scale);
         GL11.glDepthMask(false);
         mc.fontRendererObj.drawStringWithShadow(particle.text, -((float)mc.fontRendererObj.getStringWidth(particle.text) / 2.0F), (float)(-(mc.fontRendererObj.FONT_HEIGHT - 1)), 0);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDepthMask(true);
         GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.popMatrix();
      }

   }

   static class Particle {
      public float scale;
      public int ticks;
      public Location location;
      public String text;

      public Particle(Location location, String text) {
         this.location = location;
         this.text = text;
         this.ticks = 0;
         this.scale = -0.03F;
      }
   }
}
