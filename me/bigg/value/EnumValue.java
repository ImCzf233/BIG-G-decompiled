package me.bigg.value;

import me.bigg.Client;

public class EnumValue extends Value {
   private final Enum[] modes;

   public EnumValue(String name, String introduce, Enum[] modes) {
      super(name, introduce);
      this.modes = modes;
      this.setValue(modes[0]);
   }

   public Enum[] getModes() {
      return this.modes;
   }

   public Enum getMode() {
      return (Enum)this.getValue();
   }

   public void setMode(String mode) {
      if (!this.isValid(mode)) {
         Client.INSTANCE.getLogger().warning("Setting to a invalid mode at " + this.getClass().getName());
      } else {
         Enum[] var5;
         int var4 = (var5 = this.modes).length;

         for(int var3 = 0; var3 < var4; ++var3) {
            Enum v = var5[var3];
            if (v.name().equalsIgnoreCase(mode)) {
               this.setValue(v);
            }
         }

      }
   }

   public boolean isValid(String name) {
      Enum[] var5;
      int var4 = (var5 = this.modes).length;

      for(int var3 = 0; var3 < var4; ++var3) {
         Enum v = var5[var3];
         if (v.name().equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }
}
