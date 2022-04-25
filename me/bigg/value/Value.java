package me.bigg.value;

import me.bigg.Client;

public abstract class Value {
   private final String introduce;
   private final String name;
   private Object value;

   public Value(String name, String introduce) {
      this.introduce = introduce;
      this.name = name;
   }

   public String getSimpleName() {
      return this.name.toLowerCase().replaceAll(" ", "");
   }

   public String getIntroduce() {
      return this.introduce;
   }

   public String getName() {
      return this.name;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object value) {
      this.value = value;
      if (Client.INSTANCE.getFileManager() != null) {
         Client.INSTANCE.getFileManager().saveValue();
      }

   }
}
