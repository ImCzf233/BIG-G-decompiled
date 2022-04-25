package me.bigg.module.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import me.bigg.Client;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {
   private static final EnumValue modeValue = new EnumValue("Mode", "Bot mode", AntiBot.modeEnums.values());
   private static final NumValue livingTicksValue = new NumValue("Living Ticks", "Living mode custom ticks", 80.0, 0.0, 100.0, 10.0);
   private static final ArrayList ground = new ArrayList();

   public AntiBot() {
      super("Anti Bot", "Anti server bot", Category.Combat);
      this.addValues(new Value[]{modeValue, livingTicksValue});
   }

   public void onEnable() {
      super.onEnable();
      ground.clear();
   }

   public void onDisable() {
      super.onDisable();
      ground.clear();
   }

   @EventTarget(0)
   void onUpdate(UpdateEvent event) {
      this.setLabel(modeValue.getMode().name());
      if (mc.thePlayer.ticksExisted <= 1) {
         ground.clear();
      }

      if (modeValue.getValue() == AntiBot.modeEnums.Watchdog) {
         Iterator var3 = mc.theWorld.playerEntities.iterator();

         while(var3.hasNext()) {
            EntityPlayer entity = (EntityPlayer)var3.next();
            this.removeHypixelBot(entity);
         }
      }
   }

   private ArrayList getLivingPlayers() {
      return (ArrayList)Arrays.asList((EntityPlayer[])mc.theWorld.loadedEntityList.stream().filter((entity) -> {
         return entity instanceof EntityPlayer;
      }).filter((entity) -> {
         return entity != mc.thePlayer;
      }).map((entity) -> {
         return (EntityPlayer)entity;
      }).toArray((var0) -> {
         return new EntityPlayer[var0];
      }));
   }

   private static boolean inTab(EntityLivingBase entity) {
      Iterator var2 = mc.getNetHandler().getPlayerInfoMap().iterator();

      NetworkPlayerInfo playerInfo;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         Object item = var2.next();
         playerInfo = (NetworkPlayerInfo)item;
      } while(playerInfo == null || playerInfo.getGameProfile() == null || !playerInfo.getGameProfile().getName().contains(entity.getName()));

      return true;
   }

   private static boolean isMineplexNPC(EntityLivingBase entity) {
      String custom = entity.getCustomNameTag();
      return entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP) && mc.thePlayer.ticksExisted > 40 && custom.equals("");
   }

   private static boolean isHypixelNPC(EntityLivingBase entity) {
      String formatted = entity.getDisplayName().getFormattedText();
      if (!formatted.startsWith("§") && formatted.endsWith("§r")) {
         return true;
      } else {
         return ground.contains(entity.getEntityId()) ? true : formatted.contains("§8[NPC]");
      }
   }

   private boolean removeHypixelBot(EntityLivingBase entity) {
      if (entity instanceof EntityWither && entity.isInvisible()) {
         return true;
      } else if (!inTab(entity) && !isHypixelNPC(entity) && entity.isEntityAlive() && entity != mc.thePlayer) {
         mc.theWorld.removeEntity(entity);
         return true;
      } else {
         return false;
      }
   }

   public static boolean isBot(EntityLivingBase entity) {
      if (Client.INSTANCE.getModuleManager().getModule("antibot").isEnabled() && entity != mc.thePlayer) {
         if ((modeValue.getMode() == AntiBot.modeEnums.Living || modeValue.getMode() == AntiBot.modeEnums.Advanced) && entity.ticksExisted > ((Number)livingTicksValue.getValue()).intValue()) {
            return true;
         } else if (modeValue.getMode() == AntiBot.modeEnums.Advanced && !ground.contains(entity.getEntityId())) {
            return true;
         } else if (modeValue.getValue() == AntiBot.modeEnums.BrokenID && entity.getEntityId() > 1000000) {
            return true;
         } else if (modeValue.getValue() == AntiBot.modeEnums.Tab && !inTab(entity)) {
            return true;
         } else if (modeValue.getValue() == AntiBot.modeEnums.Watchdog && isHypixelNPC(entity)) {
            return true;
         } else {
            return modeValue.getValue() == AntiBot.modeEnums.Mineplex && isMineplexNPC(entity);
         }
      } else {
         return false;
      }
   }

   private static enum modeEnums {
      Living,
      Advanced,
      BrokenID,
      Tab,
      Watchdog,
      Mineplex;
   }
}
