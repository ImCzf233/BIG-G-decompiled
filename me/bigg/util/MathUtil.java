package me.bigg.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtil {
   private static final Random random = new Random();

   public static Random getRandom() {
      return random;
   }

   public static boolean isInteger(String num) {
      try {
         Integer.parseInt(num);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isDouble(String num) {
      try {
         Double.parseDouble(num);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isFloat(String num) {
      try {
         Float.parseFloat(num);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isLong(String num) {
      try {
         Long.parseLong(num);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static boolean isBoolean(String b) {
      try {
         Boolean.parseBoolean(b);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static int getRandom(int min, int max) {
      return max < min ? 0 : min + random.nextInt(max - min + 1);
   }

   public static double getRandom(double min, double max) {
      Random random = new Random();
      double range = max - min;
      double scaled = random.nextDouble() * range;
      if (scaled > max) {
         scaled = max;
      }

      double shifted = scaled + min;
      if (shifted > max) {
         shifted = max;
      }

      return shifted;
   }

   public static double roundToPlace(double value, int places) {
      if (places < 0) {
         throw new IllegalArgumentException();
      } else {
         BigDecimal bd = new BigDecimal(value);
         bd = bd.setScale(places, RoundingMode.HALF_UP);
         return bd.doubleValue();
      }
   }

   public static boolean inRange(double x, double y, double maxX, double maxY, double minX, double minY) {
      return x > minX && x < maxX && y > minY && y < maxY;
   }
}
