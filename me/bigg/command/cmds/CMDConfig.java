package me.bigg.command.cmds;

import me.bigg.Client;
import me.bigg.command.CMD;
import me.bigg.notification.Notification;

public class CMDConfig extends CMD {
   public CMDConfig() {
      super(new String[]{"config", "cfg"}, "Load local configs.", ".config load / save name");
   }

   public void onCMD(String[] texts) {
      if (texts.length != 3) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("It's should be " + this.getCaption()));
      } else if (texts[1].equalsIgnoreCase("load")) {
         Client.INSTANCE.getFileManager().loadConfig(texts[2]);
      } else if (texts[1].equalsIgnoreCase("save")) {
         Client.INSTANCE.getFileManager().saveConfig(texts[2]);
      } else {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("It's should be " + this.getCaption()));
      }

   }
}
