package me.bigg.module.assist;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import me.bigg.Client;
import me.bigg.event.OverlayEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.event.WorldReloadEvent;
import me.bigg.util.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class NetWorkAssist {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private int inComing;
   private int outGoing;
   private final ArrayList inComingList = new ArrayList();
   private final ArrayList outGoingList = new ArrayList();
   private final TimerUtil timer = new TimerUtil();
   private final TimerUtil lag = new TimerUtil();

   public void init() {
      EventManager.register(this);
      this.resetStuff();
   }

   private void resetStuff() {
      this.inComing = 0;
      this.outGoing = 0;
      this.inComingList.clear();
      this.outGoingList.clear();
      this.timer.reset();
   }

   @EventTarget
   void onWorldReload(WorldReloadEvent event) {
      this.resetStuff();
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (this.timer.hasPassed(1000L) && event.isPost()) {
         this.inComingList.add(this.inComing);
         this.inComing = 0;
         this.outGoingList.add(this.outGoing);
         this.outGoing = 0;
         this.timer.reset();
      }

   }

   @EventTarget
   void onPacket(PacketEvent event) {
      if (event.isOutGoing()) {
         ++this.outGoing;
      } else if (event.isInComing()) {
         ++this.inComing;
      }

   }

   @EventTarget(0)
   void onOverlay(OverlayEvent event) {
      ScaledResolution sr = event.getScaledResolution();
      short size;
      if ((Integer)this.inComingList.get(this.inComingList.size() - 1) == 0) {
         this.lag.reset();
         mc.getTextureManager().bindTexture(new ResourceLocation("annex/000.png"));
         size = 128;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() / 2 - size / 2, 30, 0.0F, 0.0F, size, size, (float)size, (float)size);
         GlStateManager.resetColor();
         Client.INSTANCE.getFontManager().arial18.drawStringWithShadow("Lag Detected", (double)((float)sr.getScaledWidth() / 2.0F - (float)Client.INSTANCE.getFontManager().arial18.getStringWidth("Lag Detected") / 2.0F), (double)(size - 8), -1);
      } else if (!this.lag.hasPassed(1000L)) {
         mc.getTextureManager().bindTexture(new ResourceLocation("annex/001.png"));
         size = 128;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() / 2 - size / 2, 30, 0.0F, 0.0F, size, size, (float)size, (float)size);
         GlStateManager.resetColor();
         Client.INSTANCE.getFontManager().arial18.drawStringWithShadow("Connected", (double)((float)sr.getScaledWidth() / 2.0F - (float)Client.INSTANCE.getFontManager().arial18.getStringWidth("Connected") / 2.0F), (double)(size - 8), -1);
      }

   }

   public ArrayList getInComingList() {
      return this.inComingList;
   }

   public ArrayList getOutGoingList() {
      return this.outGoingList;
   }

   public boolean isLagging() {
      return (Integer)this.inComingList.get(this.inComingList.size() - 1) == 0;
   }
}
