package me.bigg.command.cmds;

import java.util.Iterator;
import me.bigg.Client;
import me.bigg.command.CMD;
import me.bigg.module.Module;
import me.bigg.notification.Notification;
import org.lwjgl.input.Keyboard;

public class CMDBind extends CMD {
   public CMDBind() {
      super(new String[]{"bind", "b"}, "Bind modules.", ".b module key");
   }

   public void onCMD(String[] texts) {
      if (texts.length != 3) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("It's should be " + this.getCaption()));
      } else {
         int key = Keyboard.getKeyIndex(texts[2].toUpperCase());
         Iterator var4 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(var4.hasNext()) {
            Module m = (Module)var4.next();
            if (m.getSimpleName().equalsIgnoreCase(texts[1])) {
               m.setKeybinding(key);
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification(m.getName() + " is now bound to " + Keyboard.getKeyName(m.getKeybinding())));
               break;
            }
         }

      }
   }
}
