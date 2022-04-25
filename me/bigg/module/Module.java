package me.bigg.module;

import com.darkmagician6.eventapi.EventManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.command.CMD;
import me.bigg.notification.Notification;
import me.bigg.util.ColorUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.Minecraft;

public class Module {
   public static Minecraft mc = Minecraft.getMinecraft();
   private final ArrayList values = new ArrayList();
   private boolean toggled;
   private boolean hidden;
   private String label = "";
   private final String name;
   private final String introduce;
   private final Category category;
   private int keybinding;
   private float animationX;
   private float animationY;
   private final int randomColor;

   public Module(String NAME, String INTRODUCE, Category CATEGORY) {
      this.name = NAME;
      this.introduce = INTRODUCE;
      this.category = CATEGORY;
      this.hidden = false;
      this.toggled = false;
      this.keybinding = -1;
      this.randomColor = ColorUtil.getRandomColor().getRGB();
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public boolean isEnabled() {
      return this.toggled;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public void toggle() {
      this.setEnable(!this.isEnabled());
   }

   public void setEnable(boolean state) {
      if (this.toggled != state) {
         this.toggled = state;
         if (this.toggled) {
            this.stopSleep();
         } else {
            this.sleep();
         }

         if (Client.INSTANCE.getFileManager() != null) {
            Client.INSTANCE.getFileManager().saveEnabled();
         }

      }
   }

   public void coolDown(long time) {
      this.sleep();
      Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification(this.getName() + " cool down " + (float)time / 1000.0F + "s", time));
      (new Thread(() -> {
         try {
            Thread.sleep(time);
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         }

         this.stopSleep();
      })).start();
   }

   public void sleep() {
      EventManager.unregister(this);

      try {
         this.onDisable();
      } catch (Exception var2) {
         if (mc.thePlayer != null) {
            var2.printStackTrace();
         }
      }

   }

   public void stopSleep() {
      if (this.isEnabled()) {
         EventManager.register(this);

         try {
            this.onEnable();
            if (this.animationX == -1.0F) {
               this.animationX = 0.0F;
            }

            if (this.animationY == -1.0F) {
               this.animationY = 0.0F;
            }
         } catch (Exception var2) {
            if (mc.thePlayer != null) {
               var2.printStackTrace();
            }
         }
      }

   }

   public void addValues(Value... sets) {
      this.values.addAll(Arrays.asList(sets).subList(0, sets.length));
      Client.INSTANCE.getCmdManager().getCMDs().add(this.getValueCommand());
   }

   public float getAnimationX() {
      return this.animationX;
   }

   public float getAnimationY() {
      return this.animationY;
   }

   public void setAnimationX(float animationX) {
      this.animationX = animationX;
   }

   public void setAnimationY(float animationY) {
      this.animationY = animationY;
   }

   public ArrayList getValues() {
      return this.values;
   }

   public String getName() {
      return this.name;
   }

   public String getLabel() {
      return this.label;
   }

   public String getSimpleName() {
      return this.name.toLowerCase().replaceAll(" ", "");
   }

   public String getIntroduce() {
      return this.introduce;
   }

   public Category getCategory() {
      return this.category;
   }

   public int getKeybinding() {
      return this.keybinding;
   }

   public void setKeybinding(int key) {
      this.keybinding = key;
      if (Client.INSTANCE.getFileManager() != null) {
         Client.INSTANCE.getFileManager().saveKey();
      }

   }

   public void setLabel(String LABEL) {
      this.label = LABEL;
   }

   public int getRandomColor() {
      return this.randomColor;
   }

   private CMD getValueCommand() {
      return new CMD(new String[]{this.getSimpleName()}, "Setup a module", ".module value_name value") {
         public void onCMD(String[] texts) {
            if (texts.length == 3) {
               String targetName = texts[1];
               String targetValue = texts[2];
               Iterator var5 = Module.this.values.iterator();

               while(var5.hasNext()) {
                  Value value = (Value)var5.next();
                  if (value.getSimpleName().equalsIgnoreCase(targetName)) {
                     if (value instanceof BoolValue) {
                        BoolValue boolValue = (BoolValue)value;
                        boolValue.setValue(Boolean.parseBoolean(targetValue));
                        Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification(value.getName() + " is now §c" + boolValue.getValue()));
                     } else if (value instanceof NumValue) {
                        NumValue numValue = (NumValue)value;
                        double inc = numValue.getIncrement().doubleValue();
                        double min = numValue.getMinimum().doubleValue();
                        double max = numValue.getMaximum().doubleValue();
                        double target = Double.parseDouble(targetValue);
                        double afterMath = (double)Math.round(Math.max(min, Math.min(max, target)) * (1.0 / inc)) / (1.0 / inc);
                        numValue.setValue(afterMath);
                        Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification(value.getName() + " is now §c" + numValue.getValue()));
                     } else if (value instanceof EnumValue) {
                        EnumValue enumValue = (EnumValue)value;
                        enumValue.setMode(targetValue);
                        Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification(value.getName() + " is now §c" + enumValue.getMode().name()));
                     }
                  }
               }

            }
         }
      };
   }
}
