package me.bigg.util;

import me.bigg.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatUtil {
   private static final Minecraft mc = Minecraft.getMinecraft();

   public static void printChat(String message) {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
   }

   public static void printChatClientName(String message) {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§8[§7" + Client.INSTANCE.getName() + "§8]§f " + message));
   }

   public static void debug(String message) {
   }

   public static void printIRCChat(String message) {
      if (mc.thePlayer != null) {
         mc.thePlayer.playSound("random.pop", 0.7F, 1.0F);
      }

      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("§dIRC§8 > §r" + message));
   }
}
