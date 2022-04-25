package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.RenderCapeEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;

public class Cape extends Module {
   public Cape() {
      super("Cape", "Render cape", Category.Vision);
   }

   @EventTarget
   void onRenderCape(RenderCapeEvent event) {
      if (event.getPlayer() instanceof EntityPlayerSP) {
         event.setLocation(new ResourceLocation("annex/cape.png"));
      }

   }
}
