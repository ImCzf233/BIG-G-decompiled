package me.bigg.command.cmds;

import java.util.Iterator;
import me.bigg.Client;
import me.bigg.command.CMD;
import me.bigg.module.Module;
import me.bigg.notification.Notification;

public class CMDHidden extends CMD {
   public CMDHidden() {
      super(new String[]{"hide"}, "Hide modules.", ".hide module");
   }

   public void onCMD(String[] texts) {
      if (texts.length != 2) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("It's should be " + this.getCaption()));
      } else {
         Iterator var3 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(var3.hasNext()) {
            Module mod = (Module)var3.next();
            if (texts[1].equalsIgnoreCase(mod.getSimpleName())) {
               mod.setHidden(!mod.isHidden());
               Client.INSTANCE.getFileManager().saveHidden();
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification(mod.getName() + " Hidden " + texts[1]));
               break;
            }
         }

      }
   }
}
