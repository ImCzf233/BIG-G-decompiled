package me.bigg.value;

public class BoolValue extends Value {
   public BoolValue(String name, String introduce, Object enabled) {
      super(name, introduce);
      this.setValue(enabled);
   }
}
