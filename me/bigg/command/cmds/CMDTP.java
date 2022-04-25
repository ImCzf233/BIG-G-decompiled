package me.bigg.command.cmds;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.command.CMD;
import me.bigg.event.MoveEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.RenderEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.notification.Notification;
import me.bigg.util.MathUtil;
import me.bigg.util.PathUtil;
import me.bigg.util.PlayerUtil;
import me.bigg.util.RenderUtil;
import me.bigg.util.RotationUtil;
import me.bigg.util.sub.Rotation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.BlockPos;

public class CMDTP extends CMD {
   private BlockPos pos;
   private EntityPlayer player;
   private ArrayList path = new ArrayList();
   private boolean tp;

   public CMDTP() {
      super(new String[]{"teleport", "tp"}, "Teleport to target position.", ".tp (x y z) / player");
   }

   public void onCMD(String[] texts) {
      this.pos = null;
      this.player = null;
      this.path.clear();
      this.tp = false;
      if (EventManager.registered(this)) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("You are already going to , cancelled"));
         EventManager.unregister(this);
      } else if (!mc.thePlayer.onGround) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Try again on ground"));
      } else {
         String xStr;
         if (texts.length == 2) {
            xStr = texts[1];
            EntityPlayer target = null;
            Iterator var13 = mc.theWorld.playerEntities.iterator();

            while(var13.hasNext()) {
               EntityPlayer entityPlayer = (EntityPlayer)var13.next();
               if (entityPlayer.getName().equals(xStr)) {
                  target = entityPlayer;
                  break;
               }
            }

            if (target == null) {
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Can't find the player"));
            } else {
               this.player = target;
            }

            this.startTeleport();
         } else if (texts.length != 4) {
            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("It's should be " + this.getCaption()));
         } else {
            xStr = texts[1];
            String yStr = texts[2];
            String zStr = texts[3];
            float x;
            if (MathUtil.isFloat(xStr)) {
               x = Float.parseFloat(xStr);
            } else if (xStr.equals("~")) {
               x = (float)((int)mc.thePlayer.posX);
            } else {
               if (!xStr.startsWith("~") || xStr.length() <= 1 || !MathUtil.isFloat(yStr.substring(1))) {
                  Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Invalid number"));
                  return;
               }

               x = (float)((int)mc.thePlayer.posX) + Float.parseFloat(xStr.substring(1));
            }

            float y;
            if (MathUtil.isFloat(yStr)) {
               y = Float.parseFloat(yStr);
            } else if (yStr.equals("~")) {
               y = (float)((int)mc.thePlayer.posY);
            } else {
               if (!yStr.startsWith("~") || yStr.length() <= 1 || !MathUtil.isFloat(yStr.substring(1))) {
                  Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Invalid number"));
                  return;
               }

               y = (float)((int)mc.thePlayer.posY) + Float.parseFloat(yStr.substring(1));
            }

            float z;
            if (MathUtil.isFloat(zStr)) {
               z = Float.parseFloat(zStr);
            } else if (zStr.equals("~")) {
               z = (float)((int)mc.thePlayer.posZ);
            } else {
               if (!zStr.startsWith("~") || zStr.length() <= 1 || !MathUtil.isFloat(zStr.substring(1))) {
                  Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Invalid number"));
                  return;
               }

               z = (float)((int)mc.thePlayer.posZ) + Float.parseFloat(zStr.substring(1));
            }

            this.pos = new BlockPos((double)x, (double)y, (double)z);
            Block block = mc.theWorld.getBlockState(new BlockPos(this.pos)).getBlock();
            if (block != Blocks.air && block.isFullBlock()) {
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("You are trying to teleport into a block"));
               return;
            }

            PathUtil.Vec3 topFrom = new PathUtil.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            PathUtil.Vec3 to = new PathUtil.Vec3((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
            this.path = PathUtil.computePath(topFrom, to);
            this.startTeleport();
         }

      }
   }

   private void startTeleport() {
      Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Please wait for 5s.", 5000L));
      mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
      mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ, true));
      mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212015, mc.thePlayer.posZ, true));
      EventManager.register(this);
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (event.isPre()) {
         if (this.tp) {
            PathUtil.Vec3 pathElm;
            if (this.path.isEmpty()) {
               PathUtil.Vec3 topFrom = new PathUtil.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
               pathElm = new PathUtil.Vec3((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
               this.path = PathUtil.computePath(topFrom, pathElm);
            }

            if (this.path.isEmpty()) {
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Failed to teleport , may be u r too close to target position"));
            } else {
               PlayerCapabilities capabilities = new PlayerCapabilities();
               capabilities.allowFlying = true;
               capabilities.isFlying = true;
               mc.thePlayer.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(capabilities));
               Iterator var4 = this.path.iterator();

               while(var4.hasNext()) {
                  pathElm = (PathUtil.Vec3)var4.next();
                  mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
               }

               mc.thePlayer.setPosition((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
               EventManager.unregister(this);
            }
         }

      }
   }

   @EventTarget
   void onPacket(PacketEvent event) {
      if (event.isOutGoing() && (event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C00PacketKeepAlive)) {
         event.setCancelled(!this.tp);
      }

      if (RotationUtil.serverRotate(event, new Rotation(mc.thePlayer))) {
         if (!this.tp) {
            if (this.pos == null && this.player != null) {
               this.pos = this.player.getPosition().add(0, 3, 0);
            }

            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Teleporting to " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ()));
            this.tp = true;
         } else {
            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Failed to teleport due to flag"));
            EventManager.unregister(this);
         }
      }

   }

   @EventTarget
   void onRender(RenderEvent event) {
      Iterator var3 = this.path.iterator();

      while(var3.hasNext()) {
         PathUtil.Vec3 location = (PathUtil.Vec3)var3.next();
         RenderUtil.drawBoundingBox(location.getX() - mc.getRenderManager().renderPosX, location.getY() - mc.getRenderManager().renderPosY, location.getZ() - mc.getRenderManager().renderPosZ, 0.30000001192092896, 2.0, 0.1F, 0.2F, 1.0F, 0.7F);
      }

   }

   @EventTarget(0)
   void onMove(MoveEvent event) {
      if (!this.tp) {
         PlayerUtil.setSpeed(event, 0.0);
      }

   }
}
