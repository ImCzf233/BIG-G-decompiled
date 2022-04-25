package me.bigg.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class WebUtils {
   public static String get(String url) throws IOException {
      HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", "Mozilla/5.0");
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder response = new StringBuilder();

      String inputLine;
      while((inputLine = in.readLine()) != null) {
         response.append(inputLine).append("\n");
      }

      in.close();
      return response.toString();
   }

   public static String post(String url, Map requestMap, String body) throws IOException {
      HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Mozilla/5.0");
      if (requestMap != null) {
         requestMap.forEach(con::setRequestProperty);
      }

      con.setDoOutput(true);
      con.setDoInput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(body);
      wr.flush();
      wr.close();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder response = new StringBuilder();

      String inputLine;
      while((inputLine = in.readLine()) != null) {
         response.append(inputLine).append("\n");
      }

      in.close();
      return response.toString();
   }
}
