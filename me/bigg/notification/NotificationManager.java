package me.bigg.notification;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.event.OverlayEvent;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

public class NotificationManager {
   private final ArrayList notifications = new ArrayList();

   public NotificationManager() {
      EventManager.register(this);
   }

   @EventTarget(0)
   void onRenderOverlay(OverlayEvent event) {
      ScaledResolution sr = event.getScaledResolution();
      int yy = 30;
      Iterator var5 = this.notifications.iterator();

      while(var5.hasNext()) {
         Notification notification = (Notification)var5.next();
         CFontRenderer font = Client.INSTANCE.getFontManager().arial20;
         CFontRenderer small = Client.INSTANCE.getFontManager().arial16;
         String typeR = notification.getType().name().charAt(0) + notification.getType().name().substring(1).toLowerCase();
         int targetWid = Math.max(font.getStringWidth(typeR), small.getStringWidth(notification.getMessage() + 4));
         if (notification.getType() == NotificationType.ERROR) {
            Color.RED.getRGB();
         } else if (notification.getType() == NotificationType.WARN) {
            (new Color(252, 216, 0)).getRGB();
         } else {
            (new Color(255, 255, 255)).getRGB();
         }

         float y = notification.getScissor().getY();
         float targetWidth = notification.getScissor().getX();
         RenderUtil.drawRect((double)((float)sr.getScaledWidth() - targetWidth), (double)((float)sr.getScaledHeight() - y - 23.0F), (double)sr.getScaledWidth(), (double)((float)sr.getScaledHeight() - y), (new Color(7, 7, 7, 180)).getRGB());
         RenderUtil.drawRect((double)((float)sr.getScaledWidth() - Math.max(targetWidth * (float)(notification.getTime() - notification.getAnimationTimer().getDifference()) / (float)notification.getTime(), 0.0F)), (double)((float)sr.getScaledHeight() - y - 23.0F), (double)sr.getScaledWidth(), (double)((float)sr.getScaledHeight() - y), (new Color(7, 7, 7, 220)).getRGB());
         font.drawString(typeR, (float)sr.getScaledWidth() - targetWidth + 2.0F, (float)sr.getScaledHeight() - y - 20.0F, -1);
         small.drawString(notification.getMessage(), (float)sr.getScaledWidth() - targetWidth + 2.0F, (float)sr.getScaledHeight() - y - 10.0F + 3.0F, -1);
         if (notification.getAnimationTimer().hasPassed(notification.getTime())) {
            notification.getScissor().interpolate(0.0F, 0.0F, 0.15F);
         } else {
            notification.getScissor().interpolate((float)targetWid, (float)yy, 0.15F);
            yy += 23;
         }

         if (notification.getRemoveTimer().hasPassed(notification.getTime() + 500L)) {
            this.notifications.remove(notification);
         }
      }

   }

   public ArrayList getNotifications() {
      return this.notifications;
   }
}
