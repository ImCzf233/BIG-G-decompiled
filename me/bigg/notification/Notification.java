package me.bigg.notification;

import me.bigg.util.TimerUtil;
import me.bigg.util.TranslateUtil;

public class Notification {
   private String message;
   private NotificationType type;
   private final TranslateUtil scissor = new TranslateUtil(0.0F, 0.0F);
   private final TimerUtil removeTimer = new TimerUtil();
   private final TimerUtil animationTimer = new TimerUtil();
   private final long time;

   public Notification(String message) {
      this.message = message;
      this.removeTimer.reset();
      this.animationTimer.reset();
      this.time = 1000L;
      this.type = NotificationType.INFO;
   }

   public Notification(String message, NotificationType type) {
      this.message = message;
      this.removeTimer.reset();
      this.animationTimer.reset();
      this.time = 1000L;
      this.type = type;
   }

   public Notification(String message, long time) {
      this.message = message;
      this.removeTimer.reset();
      this.animationTimer.reset();
      this.time = time;
      this.type = NotificationType.INFO;
   }

   public Notification(String message, long time, NotificationType type) {
      this.message = message;
      this.removeTimer.reset();
      this.animationTimer.reset();
      this.time = time;
      this.type = type;
   }

   public NotificationType getType() {
      return this.type;
   }

   public String getMessage() {
      return this.message;
   }

   public TranslateUtil getScissor() {
      return this.scissor;
   }

   public TimerUtil getAnimationTimer() {
      return this.animationTimer;
   }

   public TimerUtil getRemoveTimer() {
      return this.removeTimer;
   }

   public long getTime() {
      return this.time;
   }
}
