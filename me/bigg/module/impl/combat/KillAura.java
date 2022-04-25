package me.bigg.module.impl.combat;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import me.bigg.Client;
import me.bigg.event.AttackEvent;
import me.bigg.event.MoveEvent;
import me.bigg.event.RenderEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.event.WorldReloadEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.misc.Teams;
import me.bigg.module.impl.movement.Scaffold;
import me.bigg.notification.Notification;
import me.bigg.util.ChatUtil;
import me.bigg.util.MathUtil;
import me.bigg.util.RaycastUtil;
import me.bigg.util.RenderUtil;
import me.bigg.util.RotationUtil;
import me.bigg.util.TimerUtil;
import me.bigg.util.sub.Motion;
import me.bigg.util.sub.Rotation;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class KillAura extends Module {
   private static EntityLivingBase target;
   private static EntityLivingBase blockTarget;
   private final ArrayList targetList = new ArrayList();
   private boolean blocking;
   private final TimerUtil blockPreventFlagTimer = new TimerUtil();
   private final Rotation currentRotationSub = new Rotation(0.0F, 0.0F);
   private final Motion myMotionSub = new Motion(0.0, 0.0, 0.0);
   private final Motion targetMotionSub = new Motion(0.0, 0.0, 0.0);
   private int index;
   private final TimerUtil switchTimer = new TimerUtil();
   private final TimerUtil attackTimer = new TimerUtil();
   private final EnumValue modeValue = new EnumValue("Mode", "Target mode", KillAura.modeEnums.values());
   private final EnumValue lockModeValue = new EnumValue("Lock mode", "Lock ables", KillAura.lockEnums.values());
   private final EnumValue swingModeValue = new EnumValue("Swing Mode", "Swing mode", KillAura.swingEnums.values());
   public static boolean blockingAnim = false;
   public final BoolValue playerValue = new BoolValue("Player", "Attack & Block player", true);
   public final BoolValue animalValue = new BoolValue("Animal", "Attack & Block animal", false);
   public final BoolValue monsterValue = new BoolValue("Monster", "Attack & Block monster", false);
   public final BoolValue neutralValue = new BoolValue("Neutral", "Attack & Block neutral", false);
   public final BoolValue preferValue = new BoolValue("Prefer", "Prefer neutral", false);
   public final BoolValue deathValue = new BoolValue("Death", "Disable on death", true);
   public final BoolValue superKBValue = new BoolValue("Super KB", "More knock back", false);
   public final BoolValue rotRandomValue = new BoolValue("Rot Random", "Randomize your fucking rotation", false);
   public final BoolValue invisibleValue = new BoolValue("Invisible", "Attack & Block invisible entity", false);
   public final BoolValue throughWallValue = new BoolValue("Through Wall", "Attack entity through wall", false);
   public final BoolValue preventFlagValue = new BoolValue("Prevent Flag", "Prevent block flags", true);
   public final BoolValue autoBlockValue = new BoolValue("Auto Block", "Auto attack entity while blocking", false);
   private final NumValue switchDelayValue = new NumValue("Switch Delay", "Delay to switch", 15.0, 0.5, 20.0, 0.5);
   private final NumValue rangeValue = new NumValue("Range", "Attack range", 4.2, 1.0, 8.0, 0.05);
   private final NumValue blockRangeValue = new NumValue("Block Range", "Block range", 5.0, 1.0, 15.0, 0.05);
   private final NumValue wallRangeValue = new NumValue("Wall Range", "Attack range when through wall", 4.2, 1.0, 5.0, 0.05);
   private final NumValue angleStepValue = new NumValue("Angle Step", "Rotation angle step", 120.0, 5.0, 180.0, 5.0);
   private final NumValue yawRandomValue = new NumValue("Yaw Rand", "Rot yaw randomize", 5.0, 1.0, 20.0, 1.0);
   private final NumValue pitchRandomValue = new NumValue("Pitch Rand", "Rot pitch randomize", 5.0, 1.0, 20.0, 1.0);
   private final NumValue hurtTimeValue = new NumValue("Hurt Time", "Attack max hurt time", 19.0, 0.0, 20.0, 1.0);
   private final NumValue chanceValue = new NumValue("Hit Chance", "Chance to hit (percentage)", 95.0, 0.0, 100.0, 5.0);
   private final NumValue apsValue = new NumValue("APS", "Attack APS", 12.0, 1.0, 20.0, 1.0);
   private final NumValue fovValue = new NumValue("Fov", "Attack Fov", 180.0, 10.0, 180.0, 10.0);
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$lockEnums;
   // $FF: synthetic field
   private static volatile int[] $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$swingEnums;

   public KillAura() {
      super("Kill Aura", "Auto attack entities around you", Category.Combat);
      this.setKeybinding(19);
      this.addValues(new Value[]{this.modeValue, this.swingModeValue, this.lockModeValue, this.apsValue, this.fovValue, this.switchDelayValue, this.wallRangeValue, this.blockRangeValue, this.rangeValue, this.angleStepValue, this.yawRandomValue, this.pitchRandomValue, this.chanceValue, this.hurtTimeValue, this.playerValue, this.animalValue, this.monsterValue, this.neutralValue, this.preferValue, this.deathValue, this.superKBValue, this.rotRandomValue, this.invisibleValue, this.preventFlagValue, this.throughWallValue, this.autoBlockValue});
   }

   public void onEnable() {
      super.onEnable();
      this.targetList.clear();
      target = null;
      blockTarget = null;
      this.blocking = mc.thePlayer.isBlocking();
      this.blockPreventFlagTimer.reset();
      this.currentRotationSub.setYaw(mc.thePlayer.rotationYaw);
      this.currentRotationSub.setPitch(mc.thePlayer.rotationPitch);
      this.myMotionSub.reset();
      this.targetMotionSub.reset();
      this.attackTimer.reset();
      this.index = 0;
      this.switchTimer.reset();
   }

   public void onDisable() {
      super.onDisable();
      this.targetList.clear();
      target = null;
      blockTarget = null;
      if (this.blocking) {
         this.unblock(true);
      }

   }

   @EventTarget
   void onWorldReload(WorldReloadEvent event) {
      if (mc.thePlayer.ticksExisted <= 1 && (Boolean)this.deathValue.getValue()) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Disabled aura due to death"));
         this.toggle();
      }

   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      this.setLabel(this.modeValue.getMode().name());
      if (event.isPre()) {
         blockTarget = null;
         Iterator var3 = mc.theWorld.getLoadedEntityList().iterator();

         Entity entity;
         EntityLivingBase livingBase;
         while(var3.hasNext()) {
            entity = (Entity)var3.next();
            if (!(Boolean)this.autoBlockValue.getValue()) {
               break;
            }

            if (entity instanceof EntityLivingBase) {
               livingBase = (EntityLivingBase)entity;
               if (this.getEntityValidBlock(livingBase)) {
                  blockTarget = livingBase;
                  break;
               }
            }
         }

         target = null;
         this.targetList.clear();
         var3 = mc.theWorld.getLoadedEntityList().iterator();

         while(var3.hasNext()) {
            entity = (Entity)var3.next();
            if (entity instanceof EntityLivingBase) {
               livingBase = (EntityLivingBase)entity;
               if (this.getEntityValid(livingBase)) {
                  this.targetList.add(livingBase);
               }
            }
         }

         if ((Boolean)this.preferValue.getValue()) {
            var3 = this.targetList.iterator();

            while(var3.hasNext()) {
               EntityLivingBase entityLivingBase = (EntityLivingBase)var3.next();
               if (entityLivingBase instanceof EntityWither) {
                  this.targetList.clear();
                  this.targetList.add(entityLivingBase);
                  break;
               }
            }
         }

         if (this.switchTimer.hasPassed((long)((Number)this.switchDelayValue.getValue()).intValue() * 100L) && this.targetList.size() > 1) {
            this.switchTimer.reset();
            ++this.index;
         }

         if (this.index >= this.targetList.size()) {
            this.index = 0;
         }

         if (!this.targetList.isEmpty()) {
            target = (EntityLivingBase)this.targetList.get(this.modeValue.getMode() == KillAura.modeEnums.Switch ? this.index : 0);
         }

         if (target == null) {
            this.currentRotationSub.setYaw(mc.thePlayer.rotationYaw);
            this.currentRotationSub.setPitch(mc.thePlayer.rotationPitch);
         } else {
            this.targetMotionSub.setTo(new Motion(target));
            double lower = (double)target.getEyeHeight() * (!target.isChild() && !(target.getEntityBoundingBox().maxY - target.getEntityBoundingBox().minY < 1.0) ? 0.5 : 2.0);
            double[] absSPX = new double[]{Math.abs(this.myMotionSub.getMotionX()), Math.abs(this.myMotionSub.getMotionZ())};
            double[] absTPX = new double[]{Math.abs(this.targetMotionSub.getMotionX()), Math.abs(this.targetMotionSub.getMotionZ())};
            double[] difference = new double[]{absSPX[0] > absTPX[0] ? absSPX[0] - absTPX[0] : absTPX[0] - absSPX[0], absSPX[1] > absTPX[1] ? absSPX[1] - absTPX[1] : absTPX[1] - absSPX[1]};
            double x = target.prevPosX + (target.posX - target.prevPosX) + difference[0] * 0.5 * (double)(this.myMotionSub.getMotionX() > 0.0 ? 1 : -1);
            double z = target.prevPosZ + (target.posZ - target.prevPosZ) + difference[1] * 0.5 * (double)(this.myMotionSub.getMotionZ() > 0.0 ? 1 : -1);
            double y = target.prevPosY + (target.posY - target.prevPosY) - lower;
            float yaw = RotationUtil.getYawDifference(this.currentRotationSub.getYaw(), x, y, z);
            float pitch = RotationUtil.getPitchDifference(this.currentRotationSub.getPitch(), x, y, z);
            float neededYaw = Math.min(Math.abs(yaw), ((Number)this.angleStepValue.getValue()).floatValue()) * (float)(yaw < 0.0F ? -1 : 1);
            float neededPitch = Math.min(Math.abs(pitch), ((Number)this.angleStepValue.getValue()).floatValue() / 2.0F) * (float)(pitch < 0.0F ? -1 : 1);
            float[] randoms = new float[]{(Boolean)this.rotRandomValue.getValue() ? (float)MathUtil.getRandom((double)(-((Number)this.yawRandomValue.getValue()).floatValue()), (double)((Number)this.yawRandomValue.getValue()).floatValue()) : 0.0F, (Boolean)this.rotRandomValue.getValue() ? (float)MathUtil.getRandom((double)(-((Number)this.pitchRandomValue.getValue()).floatValue()), (double)((Number)this.pitchRandomValue.getValue()).floatValue()) : 0.0F};
            if (Scaffold.isRotating()) {
               this.currentRotationSub.setYaw(event.getYaw());
               this.currentRotationSub.setPitch(event.getPitch());
            } else {
               boolean shouldLock = false;
               switch ($SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$lockEnums()[((lockEnums)this.lockModeValue.getMode()).ordinal()]) {
                  case 2:
                     shouldLock = target.hurtTime > 5;
                     break;
                  case 3:
                     shouldLock = RaycastUtil.raycastEntity((double)((Number)this.rangeValue.getValue()).floatValue(), this.currentRotationSub.getBoth()) != null;
                     break;
                  case 4:
                     shouldLock = target.hurtTime > 5 || RaycastUtil.raycastEntity((double)((Number)this.rangeValue.getValue()).floatValue(), this.currentRotationSub.getBoth()) != null;
               }

               if (shouldLock) {
                  neededPitch = 0.0F;
                  neededYaw = 0.0F;
               }

               event.setYaw(this.currentRotationSub.getYaw() + neededYaw + randoms[0]);
               this.currentRotationSub.add(neededYaw, 0.0F);
               if (!AutoPot.isPotting()) {
                  event.setPitch(this.currentRotationSub.getPitch() + neededPitch + randoms[1]);
                  this.currentRotationSub.add(0.0F, neededPitch);
               }
            }
         }

         if (blockTarget != null) {
            if (this.hasSword(mc.thePlayer)) {
               if (!this.blocking) {
                  this.blockPreventFlagTimer.reset();
               } else if (this.blockPreventFlagTimer.hasPassed(30L) && (Boolean)this.preventFlagValue.getValue()) {
                  this.unblock(false);
                  this.blockPreventFlagTimer.reset();
               }
            } else if (this.blocking) {
               this.unblock(true);
            }
         } else if (this.blocking) {
            this.unblock(true);
         }
      }

      if (event.isPost()) {
         if (target != null) {
            if (Scaffold.isRotating() || AutoPot.isPotting() || Client.INSTANCE.getModuleManager().getNetWorkAssist().isLagging()) {
               this.attackTimer.reset();
               return;
            }

            int minAps = Math.max(((Number)this.apsValue.getValue()).intValue() - MathUtil.getRandom(0, 3), 1);
            int maxAps = Math.min(((Number)this.apsValue.getValue()).intValue() + MathUtil.getRandom(0, 3), 20);
            int Aps = MathUtil.getRandom(minAps, maxAps);
            long delay = Math.round(1000.0 / (double)Aps);
            if (this.attackTimer.hasPassed(delay)) {
               if (this.hasSword(mc.thePlayer) && (this.blocking || mc.thePlayer.isBlocking())) {
                  this.unblock(false);
               }

               if (target.hurtResistantTime <= ((Number)this.hurtTimeValue.getValue()).intValue() && (new Random()).nextInt(101) <= ((Number)this.chanceValue.getValue()).intValue()) {
                  EventManager.call(new AttackEvent(target, true));
               }

               switch ($SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$swingEnums()[((swingEnums)this.swingModeValue.getMode()).ordinal()]) {
                  case 1:
                     mc.thePlayer.swingItem();
                     break;
                  case 2:
                     mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                     break;
                  case 3:
                     if (target.hurtTime <= 3) {
                        mc.thePlayer.swingItem();
                     }
               }

               if (target.hurtResistantTime <= ((Number)this.hurtTimeValue.getValue()).intValue() && (new Random()).nextInt(101) <= ((Number)this.chanceValue.getValue()).intValue()) {
                  if ((Boolean)this.superKBValue.getValue() && target.hurtTime <= 2) {
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                     mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                     ChatUtil.debug("super kb active");
                  }

                  mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                  EventManager.call(new AttackEvent(target, false));
               }

               if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.getHeldItem(), target.getCreatureAttribute()) > 0.0F) {
                  mc.thePlayer.onEnchantmentCritical(target);
               }

               if (mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null) {
                  mc.thePlayer.onCriticalHit(target);
               }

               this.attackTimer.reset();
            }
         }

         if (blockTarget != null && this.hasSword(mc.thePlayer) && !this.blocking) {
            this.block(true);
         }
      }

   }

   @EventTarget
   void onRender(RenderEvent event) {
      EntityLivingBase entity;
      float green;
      if (this.targetList.isEmpty()) {
         if (blockTarget != null) {
            entity = blockTarget;
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
            double height = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY + 0.1;
            double fx = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX;
            double fz = entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ;
            double width = Math.sqrt(fx * fx + fz * fz) - 0.5;
            y += height / 2.0 + height / 4.0 - 0.05;
            float red = 1.0F;
            green = entity.hurtTime > 0 ? 0.0F : 0.2F;
            green = entity.hurtTime > 0 ? 0.0F : 0.2F;
            RenderUtil.drawBoundingBox(x, y, z, width, 0.1, 1.0F, green, green, 0.7F);
         }
      } else {
         Iterator var21 = this.targetList.iterator();

         while(var21.hasNext()) {
            entity = (EntityLivingBase)var21.next();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
            double height = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY + 0.1;
            double fx = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX;
            double fz = entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ;
            double width = Math.sqrt(fx * fx + fz * fz) - 0.5;
            y += height / 2.0 + height / 4.0 - 0.05;
            green = 1.0F;
            green = entity.hurtTime > 0 ? 0.0F : 0.2F;
            float blue = entity.hurtTime > 0 ? 0.0F : 0.2F;
            if (entity == target) {
               RenderUtil.drawBoundingBox(x, y, z, width, 0.1, 1.0F, green, blue, 0.7F);
            } else {
               RenderUtil.drawBoundingBox(x, y, z, width, 0.1, 1.0F, 1.0F, 1.0F, 0.4F);
            }
         }
      }

   }

   @EventTarget(4)
   void onMove(MoveEvent event) {
      this.myMotionSub.setTo(new Motion(event.getX(), event.getY(), event.getZ()));
   }

   private void block(boolean setItemInUseCount) {
      mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
      if (setItemInUseCount) {
         blockingAnim = true;
      }

      this.blocking = true;
   }

   private void unblock(boolean setItemInUseCount) {
      mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
      if (setItemInUseCount) {
         blockingAnim = false;
      }

      this.blocking = false;
   }

   private boolean getEntityValid(EntityLivingBase entity) {
      if (mc.thePlayer.isEntityAlive() && !mc.thePlayer.isPlayerSleeping() && !mc.thePlayer.isDead && !(mc.thePlayer.getHealth() <= 0.0F) && !(mc.thePlayer.getDistanceToEntity(entity) > ((Number)this.rangeValue.getValue()).floatValue()) && entity.isEntityAlive() && !entity.isDead && !(entity.getHealth() <= 0.0F) && !(entity instanceof EntityArmorStand) && !Teams.isOnSameTeam(entity) && !AntiBot.isBot(entity) && !this.notInFov(entity) && entity != mc.thePlayer) {
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!(Boolean)this.playerValue.getValue()) {
               return false;
            }

            if (player.isPlayerSleeping()) {
               return false;
            }

            boolean wallChecks = !(Boolean)this.throughWallValue.getValue() || mc.thePlayer.getDistanceToEntity(player) > ((Number)this.wallRangeValue.getValue()).floatValue();
            if (!mc.thePlayer.canEntityBeSeen(player) && wallChecks) {
               return false;
            }

            if (player.isPotionActive(Potion.invisibility) && !(Boolean)this.invisibleValue.getValue()) {
               return false;
            }
         }

         if (entity instanceof EntityAnimal) {
            return (Boolean)this.animalValue.getValue();
         } else if (entity instanceof EntityMob) {
            return (Boolean)this.monsterValue.getValue();
         } else {
            return !(entity instanceof EntityVillager) && !(entity instanceof EntityIronGolem) && !(entity instanceof EntitySnowman) ? true : (Boolean)this.neutralValue.getValue();
         }
      } else {
         return false;
      }
   }

   private boolean getVIPValid(EntityWither entity) {
      return (Boolean)this.neutralValue.getValue() && mc.thePlayer.isEntityAlive() && !mc.thePlayer.isPlayerSleeping() && !mc.thePlayer.isDead && mc.thePlayer.getHealth() > 0.0F && mc.thePlayer.getDistanceToEntity(entity) <= ((Number)this.rangeValue.getValue()).floatValue() && !Teams.isOnSameTeam(entity) && !AntiBot.isBot(entity) && !this.notInFov(entity);
   }

   private boolean getEntityValidBlock(EntityLivingBase entity) {
      if (mc.thePlayer.isEntityAlive() && !mc.thePlayer.isPlayerSleeping() && !mc.thePlayer.isDead && !(mc.thePlayer.getHealth() <= 0.0F) && !(mc.thePlayer.getDistanceToEntity(entity) > ((Number)this.blockRangeValue.getValue()).floatValue()) && entity.isEntityAlive() && !entity.isDead && !(entity.getHealth() <= 0.0F) && !(entity instanceof EntityArmorStand) && !Teams.isOnSameTeam(entity) && !AntiBot.isBot(entity) && !this.notInFov(entity) && entity != mc.thePlayer) {
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!(Boolean)this.playerValue.getValue()) {
               return false;
            }

            if (player.isPlayerSleeping()) {
               return false;
            }

            if (player.isPotionActive(Potion.invisibility) && !(Boolean)this.invisibleValue.getValue()) {
               return false;
            }
         }

         if (entity instanceof EntityAnimal) {
            return (Boolean)this.animalValue.getValue();
         } else if (entity instanceof EntityMob) {
            return (Boolean)this.monsterValue.getValue();
         } else {
            return !(entity instanceof EntityVillager) && !(entity instanceof EntityIronGolem) && !(entity instanceof EntitySnowman) ? true : (Boolean)this.neutralValue.getValue();
         }
      } else {
         return false;
      }
   }

   private boolean hasSword(EntityPlayer playerIn) {
      return playerIn.inventory.getCurrentItem() != null && playerIn.inventory.getCurrentItem().getItem() instanceof ItemSword;
   }

   private boolean notInFov(Entity entity) {
      return !(Math.abs(RotationUtil.getYawDifference(mc.thePlayer.rotationYaw, entity.posX, entity.posY, entity.posZ)) <= ((Number)this.fovValue.getValue()).floatValue());
   }

   public static EntityLivingBase getTarget() {
      return target;
   }

   public static EntityLivingBase getBlockTarget() {
      return blockTarget;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$lockEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$lockEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[KillAura.lockEnums.values().length];

         try {
            var0[KillAura.lockEnums.Both.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[KillAura.lockEnums.Hurttime.ordinal()] = 2;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[KillAura.lockEnums.None.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[KillAura.lockEnums.Raycast.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$lockEnums = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$swingEnums() {
      int[] var10000 = $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$swingEnums;
      if (var10000 != null) {
         return var10000;
      } else {
         int[] var0 = new int[KillAura.swingEnums.values().length];

         try {
            var0[KillAura.swingEnums.Lock.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
         }

         try {
            var0[KillAura.swingEnums.None.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[KillAura.swingEnums.Normal.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
         }

         try {
            var0[KillAura.swingEnums.Server.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
         }

         $SWITCH_TABLE$me$bigg$module$impl$combat$KillAura$swingEnums = var0;
         return var0;
      }
   }

   private static enum lockEnums {
      None,
      Hurttime,
      Raycast,
      Both;
   }

   private static enum modeEnums {
      Single,
      Switch;
   }

   private static enum swingEnums {
      Normal,
      Server,
      Lock,
      None;
   }
}
