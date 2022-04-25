package me.bigg.module.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import me.bigg.Client;
import me.bigg.event.PacketEvent;
import me.bigg.event.TickEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.event.WorldReloadEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.notification.Notification;
import me.bigg.util.MathUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.EnumValue;
import me.bigg.value.Value;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Disabler extends Module {
   private List packetList = new ArrayList();
   private List hypList = new ArrayList();
   private final EnumValue modeValue = new EnumValue("Mode", "Bypass mode", Disabler.modeEnums.values());
   private final LinkedList queueUID = new LinkedList();
   private final LinkedList C00LinkedList = new LinkedList();
   int counter = 0;
   double x;
   double y;
   double z;
   float yaw;
   float pitch;
   private TimerUtil C13Check = new TimerUtil();
   private TimerUtil time = new TimerUtil();
   public boolean aba;
   private TimerUtil timer1 = new TimerUtil();
   private TimerUtil timer2 = new TimerUtil();
   private ArrayList packets = new ArrayList();
   private boolean cancel;
   public int oldPing = -1;

   public Disabler() {
      super("Disabler", "Disable anti cheats", Category.Misc);
      this.addValues(new Value[]{this.modeValue});
   }

   public void onEnable() {
      super.onEnable();
      this.counter = 0;
      this.queueUID.clear();
      this.C00LinkedList.clear();
      this.packetList.clear();
      this.hypList.clear();
      this.aba = false;
   }

   @EventTarget
   void onWorldReload(WorldReloadEvent event) {
      this.queueUID.clear();
      this.C00LinkedList.clear();
      this.packetList.clear();
      this.hypList.clear();
      this.counter = 0;
      this.aba = false;
   }

   @EventTarget
   void onUpdate(TickEvent event) {
      if (this.modeValue.getMode() != Disabler.modeEnums.Expolit || !mc.isSingleplayer()) {
         ;
      }
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name());
      if (mc.thePlayer.ticksExisted < 1) {
         this.counter = 0;
      }

      if (mc.thePlayer.isDead) {
         this.counter = 0;
      }

      if (this.timer1.hasPassed(10000L, true)) {
         this.cancel = true;
         this.timer2.reset();
      }

   }

   @EventTarget
   void onPacket(PacketEvent e) {
      Packet p = e.getPacket();
      Scaffold ab = (Scaffold)Client.INSTANCE.getModuleManager().getModule("ScaffoldA");
      if (this.modeValue.getMode() == Disabler.modeEnums.Expolit) {
         if (mc.isSingleplayer()) {
            return;
         }

         if (e.isOutGoing()) {
            if ((e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) && mc.thePlayer.ticksExisted < 50) {
               e.setCancelled(true);
            }

            if (e.getPacket() instanceof C03PacketPlayer) {
               C03PacketPlayer c03 = (C03PacketPlayer)e.getPacket();
               if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
                  e.setCancelled(true);
               }

               if (this.cancel) {
                  if (!this.timer2.hasPassed(400L, false)) {
                     if (!ab.isEnabled()) {
                        e.setCancelled(true);
                        this.packets.add(e.getPacket());
                     }
                  } else {
                     ArrayList var10000 = this.packets;
                     NetworkManager var10001 = mc.getNetHandler().getNetworkManager();
                     var10000.forEach(var10001::sendPacketNoEvent);
                     this.packets.clear();
                     this.cancel = false;
                  }
               }
            }
         }
      }

      if (e.isOutGoing()) {
         if (p instanceof C00PacketKeepAlive && this.modeValue.getMode() == Disabler.modeEnums.KeepAlive) {
            e.setCancelled(true);
         }

         if (this.modeValue.getMode() == Disabler.modeEnums.Hypixel) {
            int maxDisableDelay = 130;
            System.out.println(this.counter);
            if (this.counter == maxDisableDelay) {
               Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Watchdog has Disabled."));
            }

            if (e.isOutGoing() && e.getPacket() instanceof C0FPacketConfirmTransaction) {
               ++this.counter;
               C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction)e.getPacket();
               if (packet.getWindowId() == 0 && packet.getUid() < 0 && this.counter > maxDisableDelay) {
                  e.setCancelled(true);
                  if (!this.queueUID.isEmpty() && this.queueUID.size() >= 4) {
                     C0FPacketConfirmTransaction pollPacket = (C0FPacketConfirmTransaction)this.queueUID.poll();
                     if (pollPacket != null) {
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(pollPacket);
                     }
                  }

                  this.queueUID.offer(packet);
               }
            } else if (e.isOutGoing() && e.getPacket() instanceof C00PacketKeepAlive && !mc.isSingleplayer() && this.counter > maxDisableDelay) {
               e.setCancelled(true);
               if (!this.C00LinkedList.isEmpty() && this.C00LinkedList.size() >= 4) {
                  int sendMax = MathUtil.getRandom(1, this.C00LinkedList.size() - 1);

                  for(int i = 0; i < sendMax; ++i) {
                     C00PacketKeepAlive packet = (C00PacketKeepAlive)this.C00LinkedList.poll();
                     if (packet != null) {
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
                     }
                  }
               }

               this.C00LinkedList.add((C00PacketKeepAlive)e.getPacket());
            }
         }

         if (this.modeValue.getMode() == Disabler.modeEnums.Diff) {
            if (e.isOutGoing() && e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
               C03PacketPlayer.C06PacketPlayerPosLook packet = (C03PacketPlayer.C06PacketPlayerPosLook)e.getPacket();
               if (this.counter > 0 && packet.x == this.x && packet.y == this.y && packet.z == this.z) {
                  mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, packet.onGround));
                  e.setCancelled(true);
               }

               ++this.counter;
               if (e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook && mc.thePlayer.isRiding()) {
                  mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
               } else if (e.getPacket() instanceof C0CPacketInput && mc.thePlayer.isRiding()) {
                  mc.getNetHandler().getNetworkManager().sendPacketNoEvent(e.getPacket());
                  mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                  e.setCancelled(true);
               }
            }

            if (e.isInComing()) {
               if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                  S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook)e.getPacket();
                  this.x = s08.x;
                  this.y = s08.y;
                  this.z = s08.z;
                  this.yaw = s08.yaw;
                  this.pitch = s08.pitch;
               } else {
                  this.yaw = mc.thePlayer.rotationYaw;
                  this.pitch = mc.thePlayer.rotationPitch;
               }

               if (e.getPacket() instanceof S07PacketRespawn) {
                  this.counter = 0;
               }
            }
         }
      }

   }

   private static enum modeEnums {
      KeepAlive,
      Hypixel,
      Diff,
      Expolit;
   }
}
