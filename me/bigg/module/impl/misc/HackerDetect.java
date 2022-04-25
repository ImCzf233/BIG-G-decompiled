package me.bigg.module.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.combat.AntiBot;
import me.bigg.util.ChatUtil;
import me.bigg.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class HackerDetect extends Module {
   private static final ArrayList hackers = new ArrayList();

   public HackerDetect() {
      super("Hacker Detector", "Loser", Category.Misc);
   }

   public void onEnable() {
      super.onEnable();
      hackers.clear();
   }

   public void onDisable() {
      super.onDisable();
      hackers.clear();
   }

   @EventTarget(4)
   void onUpdate(UpdateEvent event) {
      this.setLabel("NCP");
      if (event.isPre()) {
         if (mc.thePlayer.ticksExisted <= 105) {
            hackers.clear();
         } else {
            Iterator var3 = mc.theWorld.playerEntities.iterator();

            while(true) {
               EntityPlayer player;
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 if (!var3.hasNext()) {
                                    return;
                                 }

                                 player = (EntityPlayer)var3.next();
                              } while(player == mc.thePlayer);
                           } while(player.ticksExisted < 105);
                        } while(hackers.contains(player));
                     } while(AntiBot.isBot(player));
                  } while(player.capabilities.isFlying);
               } while(player.capabilities.isCreativeMode);

               double playerSpeed = PlayerUtil.getBPS(player);
               if ((player.isUsingItem() || player.isBlocking()) && player.onGround && player.hurtTime == 0 && !player.isPotionActive(Potion.jump) && playerSpeed >= 5.6) {
                  ChatUtil.printChat("§8[§7HD§8] §c" + player.getName() + " §7has §cinvalid slow down");
                  hackers.add(player);
               }

               if (player.isSprinting() && (player.moveForward < 0.0F || player.moveForward == 0.0F && player.moveStrafing != 0.0F)) {
                  ChatUtil.printChat("§8[§7HD§8] §c" + player.getName() + " §7must be using §cOmni Sprint");
                  hackers.add(player);
               }

               if (!mc.theWorld.getCollidingBoundingBoxes(player, mc.thePlayer.getEntityBoundingBox().offset(0.0, player.motionY, 0.0)).isEmpty() && player.motionY > 0.0 && playerSpeed > 10.0) {
                  ChatUtil.printChat("§8[§7HD§8] §c" + player.getName() + " §7may using §cSpeed §7/§c Long Jump");
                  hackers.add(player);
               }

               double y = (double)Math.abs((int)player.posY);
               double lastY = (double)Math.abs((int)player.lastTickPosY);
               double yDiff = y > lastY ? y - lastY : lastY - y;
               if (yDiff > 0.0 && mc.thePlayer.onGround && player.motionY == -0.0784000015258789) {
                  ChatUtil.printChat("§8[§7HD§8] §c" + player.getName() + " §7may using §cStep §7(" + yDiff + " block)");
                  hackers.add(player);
               }

               if (player.hurtTime >= 5 && player.hurtTime <= 8 && mc.thePlayer.onGround && player.motionY == -0.0784000015258789 && player.motionX == 0.0 && player.motionZ == 0.0) {
                  ChatUtil.printChat("§8[§7HD§8] §c" + player.getName() + " §7may using §c 0% Velocity");
                  hackers.add(player);
               }
            }
         }
      }
   }

   public static ArrayList getHackers() {
      return hackers;
   }

   public static boolean isHacker(EntityPlayer p) {
      return hackers.contains(p);
   }
}
