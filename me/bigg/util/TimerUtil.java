package me.bigg.util;

public class TimerUtil {
   private long ms = this.getCurrentMS();

   private long getCurrentMS() {
      return System.currentTimeMillis();
   }

   public final long getDifference() {
      return this.getCurrentMS() - this.ms;
   }

   public final boolean hasPassed(long milliseconds) {
      return this.getCurrentMS() - this.ms > milliseconds;
   }

   public final boolean hasPassed(long milliseconds, boolean a) {
      if (System.currentTimeMillis() - this.ms > milliseconds) {
         if (a) {
            this.reset();
         }

         return true;
      } else {
         return false;
      }
   }

   public final void reset() {
      this.ms = this.getCurrentMS();
   }
}
