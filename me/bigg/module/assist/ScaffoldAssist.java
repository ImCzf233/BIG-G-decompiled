package me.bigg.module.assist;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import me.bigg.Client;
import me.bigg.event.OverlayEvent;
import me.bigg.font.cfont.CFontRenderer;
import me.bigg.module.impl.movement.Scaffold;
import me.bigg.util.RenderUtil;
import me.bigg.util.TranslateUtil;

public class ScaffoldAssist {
   private Scaffold scaffold;
   private TranslateUtil translate;
   private TranslateUtil position;

   public void init() {
      EventManager.register(this);
      this.scaffold = new Scaffold();
      this.translate = new TranslateUtil(0.0F, 0.0F);
      this.position = new TranslateUtil(0.0F, 0.0F);
   }

   @EventTarget
   void onOverlay(OverlayEvent event) {
      this.translate.interpolate((float)(Client.INSTANCE.getModuleManager().getModule("scaffold").isEnabled() ? 255 : 0), 0.0F, 0.3F);
      this.position.interpolate(0.0F, (float)(Client.INSTANCE.getModuleManager().getModule("scaffold").isEnabled() ? 80 : 60), 0.01F);
      String str = Client.INSTANCE.getModuleManager().getModule("scaffold").isEnabled() ? this.scaffold.getAllBlockCount() + " §7Left" : "Scaffold Disabled";
      CFontRenderer font = Client.INSTANCE.getFontManager().arial18;
      int width = event.getScaledResolution().getScaledWidth();
      int height = event.getScaledResolution().getScaledHeight();
      int strWidth = font.getStringWidth(str);
      int half = strWidth / 2;
      RenderUtil.drawRect((double)((float)width / 2.0F - (float)strWidth / 2.0F - 4.0F), (double)((float)height - this.position.getY() - 12.0F), (double)((float)width / 2.0F + (float)strWidth / 2.0F + 2.0F), (double)((float)height - this.position.getY() + 2.0F), (new Color(17, 17, 17, (int)this.translate.getX())).getRGB());
      Client.INSTANCE.getFontManager().arial18.drawCenteredString(str, (float)width / 2.0F, (float)height - this.position.getY() - 7.5F, (new Color(255, 255, 255, (int)this.translate.getX())).getRGB());
   }
}
