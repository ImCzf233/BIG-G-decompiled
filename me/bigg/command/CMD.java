package me.bigg.command;

import net.minecraft.client.Minecraft;

public abstract class CMD {
   public static Minecraft mc = Minecraft.getMinecraft();
   private final String[] args;
   private final String usage;
   private final String caption;

   public CMD(String[] ARGS, String USAGE, String CAPTION) {
      this.args = ARGS;
      this.usage = USAGE;
      this.caption = CAPTION;
   }

   public abstract void onCMD(String[] var1);

   public String[] getArgs() {
      return this.args;
   }

   public String getUsage() {
      return this.usage;
   }

   public String getCaption() {
      return this.caption;
   }
}
