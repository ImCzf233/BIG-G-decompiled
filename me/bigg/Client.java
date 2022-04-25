package me.bigg;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import me.bigg.command.CMDManager;
import me.bigg.file.FileManager;
import me.bigg.font.FontManager;
import me.bigg.module.Category;
import me.bigg.module.ModuleManager;
import me.bigg.notification.NotificationManager;
import me.bigg.screen.click.ClickGuiScreen;
import me.bigg.screen.click2.ClickyUI;
import me.bigg.screen.shader.impl.MainSandBox;
import me.bigg.util.HWIDUtils;
import me.bigg.util.JLoggerToLog4j;
import me.bigg.util.WebUtils;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;

public enum Client {
   INSTANCE;

   private final String NAME = "Big-G";
   private final String BUILD = "1.0";
   public static String USERNAME = "null";
   private boolean connected = false;
   private final Logger logger = new JLoggerToLog4j(LogManager.getLogger("Big-G"));
   private CMDManager cmdManager;
   private FontManager fontManager;
   private FileManager fileManager;
   private ModuleManager moduleManager;
   private NotificationManager notificationManager;
   private float clickWheel;
   private Category clickCategory;
   private ArrayList clickModuleList = new ArrayList();
   private int clickGuiX;
   private int clickGuiY;
   private int clickGuiWidth;
   private int clickGuiHeight;
   private ClickGuiScreen gui1;
   private ClickyUI gui2;
   private MainSandBox blob;
   private boolean animated = false;
   private double viaSlider;

   public void startUp() {
      Display.setTitle("Big-G b1.0 Cracked by Zhinan");

      try {
         if (WebUtils.get("https://gitee.com/oahdiabcbaiw/uyhuh/raw/master/README.txt").contains(HWIDUtils.getHWID())) {
            this.fontManager = new FontManager();
            this.cmdManager = new CMDManager();
            this.moduleManager = new ModuleManager();
            this.fileManager = new FileManager();
            this.notificationManager = new NotificationManager();
            this.blob = new MainSandBox();
            this.clickCategory = Category.Combat;
            this.clickGuiX = 5;
            this.clickGuiY = 5;
            this.clickGuiWidth = 310;
            this.clickGuiHeight = 200;
            ClickGuiScreen.setExiting(false);
            this.gui1 = new ClickGuiScreen();
            this.gui2 = new ClickyUI();
            this.viaSlider = 26.56382978723404;
            this.fileManager.loadEnabled();
            this.fileManager.loadValue();
            this.fileManager.loadKey();
            this.fileManager.loadHidden();
            this.fileManager.loadTargetHudPosition();
            this.fileManager.loadClickGui();
            Object aa = WebUtils.get("https://gitee.com/oahdiabcbaiw/uyhuh/raw/master/README.txt").split(",");
            String[] var5 = (String[]) aa;
            int var4 = ((String[]) aa).length;

            for(int var3 = 0; var3 < var4; ++var3) {
               Object line = var5[var3];
               if (((String) line).contains(HWIDUtils.getHWID())) {
                  USERNAME = ((String) line).split(":")[0];
               }
            }
         } else {
            JOptionPane.showInputDialog((Component)null, "鎵剧\ue178鐞嗗摗婵�娲�", HWIDUtils.getHWID());
            System.exit(0);
         }
      } catch (NoSuchAlgorithmException | IOException | HeadlessException var6) {
         JOptionPane.showMessageDialog((Component)null, "ERROR");
         System.exit(0);
      }

   }

   public void shutDown() {
      this.fileManager.saveEnabled();
      this.fileManager.saveValue();
      this.fileManager.saveKey();
      this.fileManager.saveHidden();
      this.fileManager.saveTargetHudPosition();
      this.fileManager.saveClickGuiInfo();
   }

   public ClickGuiScreen getgui1() {
      return this.gui1;
   }

   public ClickyUI getgui2() {
      return this.gui2;
   }

   public String getName() {
      return "Big-G Cracked by Zhinan";
   }

   public String getBuild() {
      return "1.0";
   }

   public boolean isConnected() {
      return this.connected;
   }

   public void setConnected(boolean connected) {
      this.connected = connected;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public CMDManager getCmdManager() {
      return this.cmdManager;
   }

   public FontManager getFontManager() {
      return this.fontManager;
   }

   public FileManager getFileManager() {
      return this.fileManager;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public NotificationManager getNotificationManager() {
      return this.notificationManager;
   }

   public int getClickGuiX() {
      return this.clickGuiX;
   }

   public int getClickGuiY() {
      return this.clickGuiY;
   }

   public void setClickGuiX(int clickGuiX) {
      this.clickGuiX = clickGuiX;
   }

   public void setClickGuiY(int clickGuiY) {
      this.clickGuiY = clickGuiY;
   }

   public int getClickGuiWidth() {
      return this.clickGuiWidth;
   }

   public void setClickGuiWidth(int clickGuiWidth) {
      this.clickGuiWidth = clickGuiWidth;
   }

   public int getClickGuiHeight() {
      return this.clickGuiHeight;
   }

   public void setClickGuiHeight(int clickGuiHeight) {
      this.clickGuiHeight = clickGuiHeight;
   }

   public Category getClickCategory() {
      return this.clickCategory;
   }

   public void setClickCategory(Category clickCategory) {
      this.clickCategory = clickCategory;
   }

   public ArrayList getClickModuleList() {
      return this.clickModuleList;
   }

   public void setClickModuleList(ArrayList clickModuleList) {
      this.clickModuleList = clickModuleList;
   }

   public float getClickWheel() {
      return this.clickWheel;
   }

   public void setClickWheel(float clickWheel) {
      this.clickWheel = clickWheel;
   }

   public MainSandBox getBlob() {
      return this.blob;
   }

   public boolean isAnimated() {
      return this.animated;
   }

   public void setAnimated(boolean animated) {
      this.animated = animated;
   }

   public double getVia() {
      return this.viaSlider;
   }

   public void setVia(double version) {
      this.viaSlider = version;
   }
}
