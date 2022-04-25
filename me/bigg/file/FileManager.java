package me.bigg.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.module.Module;
import me.bigg.module.impl.vision.TargetHud;
import me.bigg.notification.Notification;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class FileManager {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private final String fileLocation;

   public FileManager() {
      this.fileLocation = mc.mcDataDir.getAbsolutePath() + "/" + Client.INSTANCE.getName();
      File file = new File(this.fileLocation);
      if (!file.exists()) {
         file.mkdirs();
      }

      File cfg = new File(this.fileLocation + "/Config");
      if (!cfg.exists()) {
         cfg.mkdirs();
      }

   }

   public void saveEnabled() {
      File f = new File(this.fileLocation + "/Enabled.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
         }

         PrintWriter pw = new PrintWriter(f);
         Iterator var4 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(var4.hasNext()) {
            Module m = (Module)var4.next();
            pw.print(m.getSimpleName() + ":" + m.isEnabled() + "\n");
         }

         pw.close();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void saveKey() {
      File f2 = new File(this.fileLocation + "/Key.txt");

      try {
         if (!f2.exists()) {
            f2.createNewFile();
         }

         PrintWriter pw = new PrintWriter(f2);
         Iterator var4 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(var4.hasNext()) {
            Module m = (Module)var4.next();
            String keyName = m.getKeybinding() < 0 ? "None" : Keyboard.getKeyName(m.getKeybinding());
            pw.write(m.getSimpleName() + ":" + keyName + "\n");
         }

         pw.close();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public void saveValue() {
      File f3 = new File(this.fileLocation + "/Settings.txt");

      try {
         if (!f3.exists()) {
            f3.createNewFile();
         }

         PrintWriter pw = new PrintWriter(f3);
         Iterator var4 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(var4.hasNext()) {
            Module m = (Module)var4.next();
            Iterator var6 = m.getValues().iterator();

            while(var6.hasNext()) {
               Value value = (Value)var6.next();
               pw.write(m.getSimpleName() + ":" + value.getSimpleName() + ":" + value.getValue() + "\n");
            }
         }

         pw.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public void saveHidden() {
      File f = new File(this.fileLocation + "/Hidden.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
         }

         PrintWriter pw = new PrintWriter(f);
         Iterator var4 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

         while(var4.hasNext()) {
            Module m = (Module)var4.next();
            pw.print(m.getSimpleName() + ":" + m.isHidden() + "\n");
         }

         pw.close();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void saveTargetHudPosition() {
      File f = new File(this.fileLocation + "/TargetHudPosition.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
         }

         PrintWriter pw = new PrintWriter(f);
         pw.print(TargetHud.getHudX() + ":" + TargetHud.getHudY() + "\n");
         pw.close();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public void loadEnabled() {
      File f = new File(this.fileLocation + "/Enabled.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
            return;
         }

         BufferedReader br = new BufferedReader(new FileReader(f));

         String line;
         while((line = br.readLine()) != null) {
            if (line.contains(":")) {
               String[] split = line.split(":");
               if (Client.INSTANCE.getModuleManager().getModule(split[0]) != null) {
                  Client.INSTANCE.getModuleManager().getModule(split[0]).setEnable(Boolean.parseBoolean(split[1]));
               }
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void loadKey() {
      File f2 = new File(this.fileLocation + "/Key.txt");

      try {
         if (!f2.exists()) {
            f2.createNewFile();
            return;
         }

         BufferedReader br = new BufferedReader(new FileReader(f2));

         String line;
         while((line = br.readLine()) != null) {
            if (line.contains(":")) {
               String[] split = line.split(":");
               Module m = Client.INSTANCE.getModuleManager().getModule(split[0]);
               int key = Keyboard.getKeyIndex(split[1]);
               if (m != null && key >= 0) {
                  m.setKeybinding(key);
               }
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public void loadValue() {
      File f3 = new File(this.fileLocation + "/Settings.txt");

      try {
         if (!f3.exists()) {
            f3.createNewFile();
         } else {
            BufferedReader br = new BufferedReader(new FileReader(f3));

            while(true) {
               String line;
               String values;
               Module m;
               do {
                  do {
                     if ((line = br.readLine()) == null) {
                        return;
                     }
                  } while(!line.contains(":"));

                  String name = line.split(":")[0];
                  values = line.split(":")[1];
                  m = Client.INSTANCE.getModuleManager().getModule(name);
               } while(m == null);

               Iterator var8 = m.getValues().iterator();

               while(var8.hasNext()) {
                  Value value = (Value)var8.next();
                  if (value.getSimpleName().equalsIgnoreCase(values)) {
                     if (value instanceof BoolValue) {
                        BoolValue boolValue = (BoolValue)value;
                        boolValue.setValue(Boolean.parseBoolean(line.split(":")[2]));
                     } else if (value instanceof NumValue) {
                        NumValue numValue = (NumValue)value;
                        numValue.setValue(Double.parseDouble(line.split(":")[2]));
                     } else if (value instanceof EnumValue) {
                        EnumValue enumValue = (EnumValue)value;
                        enumValue.setMode(line.split(":")[2]);
                     }
                  }
               }
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }
   }

   public void loadHidden() {
      File f = new File(this.fileLocation + "/Hidden.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
            return;
         }

         BufferedReader br = new BufferedReader(new FileReader(f));

         String line;
         while((line = br.readLine()) != null) {
            if (line.contains(":")) {
               String[] split = line.split(":");
               if (Client.INSTANCE.getModuleManager().getModule(split[0]) != null) {
                  Client.INSTANCE.getModuleManager().getModule(split[0]).setHidden(Boolean.parseBoolean(split[1]));
               }
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void loadTargetHudPosition() {
      File f = new File(this.fileLocation + "/TargetHudPosition.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
            return;
         }

         BufferedReader br = new BufferedReader(new FileReader(f));

         String line;
         while((line = br.readLine()) != null) {
            if (line.contains(":")) {
               String[] split = line.split(":");
               TargetHud.setHudX(Integer.parseInt(split[0]));
               TargetHud.setHudY(Integer.parseInt(split[1]));
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void saveClickGuiInfo() {
      File f = new File(this.fileLocation + "/ClickGui.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
         }

         PrintWriter pw = new PrintWriter(f);
         pw.print(Client.INSTANCE.getClickGuiX() + ":" + Client.INSTANCE.getClickGuiY() + ":" + Client.INSTANCE.getClickGuiWidth() + ":" + Client.INSTANCE.getClickGuiHeight() + "\n");
         pw.close();
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public void loadClickGui() {
      File f = new File(this.fileLocation + "/ClickGui.txt");

      try {
         if (!f.exists()) {
            f.createNewFile();
            return;
         }

         BufferedReader br = new BufferedReader(new FileReader(f));

         String line;
         while((line = br.readLine()) != null) {
            if (line.contains(":")) {
               String[] split = line.split(":");
               Client.INSTANCE.setClickGuiX(Integer.parseInt(split[0]));
               Client.INSTANCE.setClickGuiY(Integer.parseInt(split[1]));
               Client.INSTANCE.setClickGuiWidth(Integer.parseInt(split[2]));
               Client.INSTANCE.setClickGuiHeight(Integer.parseInt(split[3]));
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void saveConfig(String configName) {
      if (configName.contains("^")) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Config name can't contains \"^\"", 2000L));
      } else {
         File settingsFile = new File(this.fileLocation + "/Config/" + configName + "^" + "Settings.txt");

         try {
            if (!settingsFile.exists()) {
               settingsFile.createNewFile();
            }

            PrintWriter pw = new PrintWriter(settingsFile);
            Iterator var5 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(true) {
               if (!var5.hasNext()) {
                  pw.close();
                  break;
               }

               Module m = (Module)var5.next();
               Iterator var7 = m.getValues().iterator();

               while(var7.hasNext()) {
                  Value value = (Value)var7.next();
                  pw.write(m.getSimpleName() + ":" + value.getSimpleName() + ":" + value.getValue() + "\n");
               }
            }
         } catch (Exception var9) {
            var9.printStackTrace();
            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Failed to save \"" + configName + "\" config"));
            return;
         }

         File enableFile = new File(this.fileLocation + "/Config" + "/" + configName + "^" + "Enabled.txt");

         try {
            if (!enableFile.exists()) {
               enableFile.createNewFile();
            }

            PrintWriter pw = new PrintWriter(enableFile);
            Iterator var13 = Client.INSTANCE.getModuleManager().getModuleList().iterator();

            while(var13.hasNext()) {
               Module m = (Module)var13.next();
               pw.print(m.getSimpleName() + ":" + m.isEnabled() + "\n");
            }

            pw.close();
         } catch (Exception var8) {
            var8.printStackTrace();
            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Failed to save \"" + configName + "\" config"));
         }

         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Successful to save \"" + configName + "\" config"));
      }
   }

   public void loadConfig(String configName) {
      File settingsFile = new File(this.fileLocation + "/Config/" + configName + "^" + "Settings" + ".txt");
      if (settingsFile.exists()) {
         Module m;
         try {
            BufferedReader br = new BufferedReader(new FileReader(settingsFile));

            String line;
            while((line = br.readLine()) != null) {
               if (line.contains(":")) {
                  String name = line.split(":")[0];
                  String values = line.split(":")[1];
                  m = Client.INSTANCE.getModuleManager().getModule(name);
                  if (m != null) {
                     Iterator var9 = m.getValues().iterator();

                     while(var9.hasNext()) {
                        Value value = (Value)var9.next();
                        if (value.getSimpleName().equalsIgnoreCase(values)) {
                           if (value instanceof BoolValue) {
                              BoolValue boolValue = (BoolValue)value;
                              boolValue.setValue(Boolean.parseBoolean(line.split(":")[2]));
                           } else if (value instanceof NumValue) {
                              NumValue numValue = (NumValue)value;
                              numValue.setValue(Double.parseDouble(line.split(":")[2]));
                           } else if (value instanceof EnumValue) {
                              EnumValue enumValue = (EnumValue)value;
                              enumValue.setMode(line.split(":")[2]);
                           }
                        }
                     }
                  }
               }
            }
         } catch (Exception var12) {
            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Failed to load \"" + configName + "\" config"));
            var12.printStackTrace();
            return;
         }

         File enabledFile = new File(this.fileLocation + "/Config/" + configName + "^" + "Enabled" + ".txt");
         if (enabledFile.exists()) {
            try {
               BufferedReader br = new BufferedReader(new FileReader(enabledFile));

               String line;
               while((line = br.readLine()) != null) {
                  if (line.contains(":")) {
                     String[] split = line.split(":");
                     m = Client.INSTANCE.getModuleManager().getModule(split[0]);
                     boolean state = Boolean.parseBoolean(split[1]);
                     if (m != null) {
                        m.setEnable(state);
                     }
                  }
               }
            } catch (Exception var11) {
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Failed to load \"" + configName + "\" config"));
               var11.printStackTrace();
               return;
            }
         }

         this.saveEnabled();
         this.saveValue();
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Successful to load \"" + configName + "\" config"));
      } else {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("\"" + configName + "\"" + " is not exists", 2000L));
      }
   }
}
