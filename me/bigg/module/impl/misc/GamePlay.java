package me.bigg.module.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.Client;
import me.bigg.event.ChatEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.event.WorldReloadEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.notification.Notification;
import me.bigg.util.MathUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.IChatComponent;

public class GamePlay extends Module {
   private final NumValue delayValue = new NumValue("Delay", "Play delay", 1.0, 0.1, 5.0, 0.1);
   private final BoolValue autoLValue = new BoolValue("Auto L", "L", true);
   private final BoolValue autoGGValue = new BoolValue("Auto GG", "GG", true);
   private final BoolValue autoPlayValue = new BoolValue("Auto Play", "Play", true);
   private final ArrayList attacked = new ArrayList();

   public GamePlay() {
      super("Game Play", "Play for you", Category.Misc);
      this.addValues(new Value[]{this.autoLValue, this.autoGGValue, this.autoPlayValue, this.delayValue});
   }

   public void onEnable() {
      super.onEnable();
      this.attacked.clear();
   }

   @EventTarget
   void onWorldReload(WorldReloadEvent event) {
      if (!this.attacked.isEmpty()) {
         this.attacked.clear();
      }

   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (mc.thePlayer.ticksExisted <= 3 && !this.attacked.isEmpty()) {
         this.attacked.clear();
      }

      if ((Boolean)this.autoLValue.getValue()) {
         Iterator var3 = this.attacked.iterator();

         while(var3.hasNext()) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)var3.next();
            if (entityLivingBase instanceof EntityPlayer && (entityLivingBase.isDead || entityLivingBase.getHealth() <= 0.0F)) {
               mc.thePlayer.sendChatMessage("/ac " + entityLivingBase.getName() + " L");
               this.attacked.remove(entityLivingBase);
               break;
            }
         }

      }
   }

   @EventTarget
   void onChat(ChatEvent event) {
      Iterator var3 = event.getChatComponent().getSiblings().iterator();

      while(var3.hasNext()) {
         IChatComponent component = (IChatComponent)var3.next();
         ClickEvent clickEvent = component.getChatStyle().getChatClickEvent();
         if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND && clickEvent.getValue().startsWith("/play ")) {
            String[] ggMeme = new String[]{"I am the last man standing", "I am the best player", "Imagine hacking", "I don't cheat , just a good gaming chair", "You're using optifine , so you're hacking (YT:Tenebrous)", "I click like 17 , with VAPE V4", "gg losers , better luck next time", "I never cheat , it's all legit", "Watchdog best anti cheat", "Imagine hypixel can't patch a disabler by a small dev team from CHINA", "Joe Biden has Alzheimer's", "Oh hey wtf I won", "Wen will I get watchdog bans", "Feels like god mode", "Can you beat me", "EZ game", "Hello I am testing watchdog for anti cheat development team don't ban me", "Arithmo buy COVID-19 vaccine made in CHINA to fix your mom's body LOL", "It's ALL LEGIT!!", "Cheating is fair game advantages", "Unfair game advantages", "Miss due to spread", "Get good get skeet.cc"};
            if ((Boolean)this.autoGGValue.getValue()) {
               mc.thePlayer.sendChatMessage("/ac GG , " + ggMeme[MathUtil.getRandom().nextInt(ggMeme.length)]);
            }

            if (!(Boolean)this.autoPlayValue.getValue()) {
               return;
            }

            Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Auto play in " + ((Number)this.delayValue.getValue()).intValue() + "s", (long)((Number)this.delayValue.getValue()).intValue() * 1000L));
            (new Thread(() -> {
               String value = clickEvent.getValue();

               try {
                  Thread.sleep((long)((Number)this.delayValue.getValue()).intValue() * 1000L);
               } catch (InterruptedException var4) {
                  var4.printStackTrace();
               }

               mc.thePlayer.sendChatMessage(value);
            })).start();
         }
      }

   }

   @EventTarget
   void onPacket(PacketEvent event) {
      Packet packet = event.getPacket();
      if (event.isOutGoing() && packet instanceof C02PacketUseEntity) {
         C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packet;
         if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
            Entity targetEntity = mc.theWorld.getEntityByID(c02PacketUseEntity.getEntityId());
            EntityLivingBase entityLivingBase = null;
            if (targetEntity instanceof EntityLivingBase) {
               entityLivingBase = (EntityLivingBase)targetEntity;
            }

            if (entityLivingBase == null) {
               return;
            }

            if (!this.attacked.contains(entityLivingBase)) {
               this.attacked.add(entityLivingBase);
            }
         }
      }

   }
}
