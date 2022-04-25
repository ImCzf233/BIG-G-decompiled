package me.bigg.command.cmds;

import java.util.Iterator;
import me.bigg.Client;
import me.bigg.command.CMD;
import me.bigg.module.Module;
import me.bigg.notification.Notification;

public class CMDToggle extends CMD {
   public CMDToggle() {
      super(new String[]{"toggle", "t"}, "Toggle modules.", ".t module");
   }

   public void onCMD(String[] texts) {
      if (texts.length != 2) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("It's should be " + this.getCaption()));
      } else {
         Iterator var3 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(var3.hasNext()) {
            Module mod = (Module)var3.next();
            if (texts[1].equalsIgnoreCase(mod.getSimpleName())) {
               mod.setEnable(!mod.isEnabled());
               Client.INSTANCE.getFileManager().saveEnabled();
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification(mod.getName() + " is now " + (mod.isEnabled() ? "enabled" : "disabled")));
               break;
            }
         }

      }
   }
}
