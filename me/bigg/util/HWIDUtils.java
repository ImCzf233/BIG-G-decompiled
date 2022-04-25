package me.bigg.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HWIDUtils {
   public static String getHWID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
      Object s = new StringBuilder();
      Object main = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME");
      Object bytes = main.getBytes("UTF-8");
      Object messageDigest = MessageDigest.getInstance("MD5");
      Object md5 = messageDigest.digest(bytes);
      int i = 0;
      byte[] var9 = md5;
      int var8 = md5.length;

      for(int var7 = 0; var7 < var8; ++var7) {
         int b = var9[var7];
         s.append(Integer.toHexString(b & 255 | 768), 0, 3);
         if (i != md5.length - 1) {
            s.append("-");
         }

         ++i;
      }

      return "39a-32a-34f-3da-392-3fc-344-3c7-342-329-310-38b-337-339-334-323";
   }
}
