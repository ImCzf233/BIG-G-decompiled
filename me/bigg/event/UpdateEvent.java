package me.bigg.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.Minecraft;

public class UpdateEvent implements Event {
   private final boolean PRE;
   private float YAW;
   private float PITCH;
   private double X;
   private double Y;
   private double Z;
   private boolean GROUND;
   private static float RENDERPREVYAW;
   private static float RENDERYAW;
   private static float RENDERPREVPITCH;
   private static float RENDERPITCH;

   public UpdateEvent(float yaw, float pitch, double posX, double posY, double posZ, boolean ground) {
      this.YAW = yaw;
      this.PITCH = pitch;
      this.GROUND = ground;
      this.X = posX;
      this.Y = posY;
      this.Z = posZ;
      this.PRE = true;
   }

   public UpdateEvent(float yaw, float pitch) {
      RENDERPREVYAW = RENDERYAW;
      RENDERYAW = yaw;
      RENDERPREVPITCH = RENDERPITCH;
      RENDERPITCH = pitch;
      this.PRE = false;
   }

   public static float getRenderYaw() {
      return RENDERYAW;
   }

   public static float getRenderPitch() {
      return RENDERPITCH;
   }

   public static float getPrevRenderYaw() {
      return RENDERPREVYAW;
   }

   public static float getPrevRenderPitch() {
      return RENDERPREVPITCH;
   }

   public boolean isPre() {
      return this.PRE;
   }

   public boolean isPost() {
      return !this.PRE;
   }

   public double getX() {
      return this.X;
   }

   public void setX(double posX) {
      this.X = posX;
   }

   public double getY() {
      return this.Y;
   }

   public void setY(double posY) {
      this.Y = posY;
   }

   public double getZ() {
      return this.Z;
   }

   public void setZ(double posZ) {
      this.Z = posZ;
   }

   public float getYaw() {
      return this.YAW;
   }

   public void setYaw(float yaw) {
      this.YAW = yaw;
      Minecraft.getMinecraft().thePlayer.prevRenderYawOffset = RENDERPREVYAW;
      Minecraft.getMinecraft().thePlayer.renderYawOffset = RENDERYAW;
      Minecraft.getMinecraft().thePlayer.prevRotationYawHead = RENDERPREVYAW;
      Minecraft.getMinecraft().thePlayer.rotationYawHead = RENDERYAW;
   }

   public float getPitch() {
      return this.PITCH;
   }

   public void setPitch(float pitch) {
      this.PITCH = pitch;
   }

   public boolean isOnGround() {
      return this.GROUND;
   }

   public void setOnGround(boolean ground) {
      this.GROUND = ground;
   }
}
