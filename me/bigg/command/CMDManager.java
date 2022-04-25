package me.bigg.command;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.bigg.command.cmds.CMDBind;
import me.bigg.command.cmds.CMDConfig;
import me.bigg.command.cmds.CMDHidden;
import me.bigg.command.cmds.CMDTP;
import me.bigg.command.cmds.CMDToggle;

public class CMDManager {
   private final List cmds = new CopyOnWriteArrayList();

   public CMDManager() {
      this.cmds.add(new CMDConfig());
      this.cmds.add(new CMDToggle());
      this.cmds.add(new CMDBind());
      this.cmds.add(new CMDTP());
      this.cmds.add(new CMDHidden());
   }

   public List getCMDs() {
      return this.cmds;
   }

   public CMD getCMD(String name) {
      Iterator var3 = this.cmds.iterator();

      while(var3.hasNext()) {
         CMD cmd = (CMD)var3.next();
         String[] var7;
         int var6 = (var7 = cmd.getArgs()).length;

         for(int var5 = 0; var5 < var6; ++var5) {
            String str = var7[var5];
            if (str.equalsIgnoreCase(name)) {
               return cmd;
            }
         }
      }

      return null;
   }
}
