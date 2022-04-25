package me.bigg.value;

public class NumValue extends Value {
   private final Number min;
   private final Number max;
   private final Number inc;

   public NumValue(String name, String introduce, Number value, Number min, Number max, Number inc) {
      super(name, introduce);
      this.setValue(value);
      this.min = min;
      this.max = max;
      this.inc = inc;
   }

   public Number getMinimum() {
      return this.min;
   }

   public Number getMaximum() {
      return this.max;
   }

   public Number getIncrement() {
      return this.inc;
   }
}
