package me.bigg.util;

public class UnicodeUtil {
   public static String gbEncoding(String gbString) {
      char[] utfBytes = gbString.toCharArray();
      StringBuilder unicodeBytes = new StringBuilder();
      char[] var6 = utfBytes;
      int var5 = utfBytes.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         char utfByte = var6[var4];
         String hexB = Integer.toHexString(utfByte);
         if (hexB.length() <= 2) {
            hexB = "00" + hexB;
         }

         unicodeBytes.append("\\u").append(hexB);
      }

      return unicodeBytes.toString();
   }

   public static String decodeUnicode(String dataStr) {
      int start = 0;

      int end;
      StringBuilder buffer;
      for(buffer = new StringBuilder(); start > -1; start = end) {
         end = dataStr.indexOf("\\u", start + 2);
         String charStr;
         if (end == -1) {
            charStr = dataStr.substring(start + 2);
         } else {
            charStr = dataStr.substring(start + 2, end);
         }

         char letter = (char)Integer.parseInt(charStr, 16);
         buffer.append(letter);
      }

      return buffer.toString();
   }
}
