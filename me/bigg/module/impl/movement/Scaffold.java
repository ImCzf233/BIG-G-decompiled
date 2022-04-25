package me.bigg.module.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Arrays;
import java.util.List;
import me.bigg.Client;
import me.bigg.event.SafeWalkEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.misc.Disabler;
import me.bigg.module.impl.movement.speed.Speed;
import me.bigg.notification.Notification;
import me.bigg.util.InventoryUtil;
import me.bigg.util.MathUtil;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import me.bigg.util.sub.Rotation;
import me.bigg.value.BoolValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

public class Scaffold extends Module {
   private BlockData data;
   private int slot;
   private int towerTick;
   private static final Rotation rotation = new Rotation(999.0F, 999.0F);
   private final TimerUtil timer = new TimerUtil();
   private final TimerUtil towerTimer = new TimerUtil();
   private double posY;
   private int offGroundTicks;
   private final NumValue delayValue = new NumValue("Delay", "Place delay", 0.5, 0.0, 10.0, 0.5);
   private final NumValue boostValue = new NumValue("Tower Boost", "Tower boost", 1.0, 0.0, 5.0, 0.1);
   private final NumValue timesValue = new NumValue("Tower Times", "Tower times to stop", 8.0, 1.0, 15.0, 1.0);
   private final BoolValue towerMoveValue = new BoolValue("Tower Move", "fuck you", true);
   private final BoolValue positionValue = new BoolValue("Position", "Tower reset position", true);
   private final BoolValue keepYValue = new BoolValue("Keep Y", "Place at a same Y", false);
   private final BoolValue safeWalkValue = new BoolValue("Safe Walk", "Won't fall", true);
   private final BoolValue sprintValue = new BoolValue("Sprint", "Scaffold while sprinting", true);
   private final BoolValue swingValue = new BoolValue("Swing", "Swing item", true);
   private final BoolValue towerValue = new BoolValue("Tower", "Just move up", true);
   private final BoolValue eagleValue = new BoolValue("Eagle", "Legit be like", false);
   private final List blacklisted;

   public Scaffold() {
      super("Scaffold", "Place block with dick", Category.Movement);
      this.blacklisted = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.ender_chest, Blocks.yellow_flower, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.crafting_table, Blocks.snow_layer, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.cactus, Blocks.lever, Blocks.activator_rail, Blocks.rail, Blocks.detector_rail, Blocks.golden_rail, Blocks.furnace, Blocks.ladder, Blocks.oak_fence, Blocks.redstone_torch, Blocks.iron_trapdoor, Blocks.trapdoor, Blocks.tripwire_hook, Blocks.hopper, Blocks.acacia_fence_gate, Blocks.birch_fence_gate, Blocks.dark_oak_fence_gate, Blocks.jungle_fence_gate, Blocks.spruce_fence_gate, Blocks.oak_fence_gate, Blocks.dispenser, Blocks.sapling, Blocks.tallgrass, Blocks.deadbush, Blocks.web, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.nether_brick_fence, Blocks.vine, Blocks.double_plant, Blocks.flower_pot, Blocks.beacon, Blocks.pumpkin, Blocks.lit_pumpkin);
      this.setKeybinding(34);
      this.addValues(new Value[]{this.boostValue, this.timesValue, this.delayValue, this.safeWalkValue, this.keepYValue, this.towerMoveValue, this.positionValue, this.towerValue, this.sprintValue, this.swingValue, this.eagleValue});
   }

   public void onEnable() {
      super.onEnable();
      Module[] clashes = new Module[]{Client.INSTANCE.getModuleManager().getModule("Fly"), Client.INSTANCE.getModuleManager().getModule("Speed"), Client.INSTANCE.getModuleManager().getModule("Longjump")};
      int count = 0;
      Module[] var6 = clashes;
      int var5 = clashes.length;

      for(int var4 = 0; var4 < var5; ++var4) {
         Module m = var6[var4];
         if (m.isEnabled() && (!(Boolean)this.keepYValue.getValue() || !(m instanceof Speed))) {
            m.toggle();
            ++count;
         }
      }

      if (count > 0) {
         Client.INSTANCE.getNotificationManager().getNotifications().add(new Notification("Disabled " + count + " clash modules"));
      }

      this.data = null;
      this.slot = -1;
      rotation.setYaw(999.0F);
      rotation.setPitch(999.0F);
      this.timer.reset();
      this.towerTimer.reset();
      this.posY = mc.thePlayer.posY;
      this.towerTick = 0;
   }

   public void onDisable() {
      super.onDisable();
      mc.timer.timerSpeed = 1.0F;
      rotation.setYaw(999.0F);
      rotation.setPitch(999.0F);
      if ((Boolean)this.eagleValue.getValue()) {
         mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
      }

   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      mc.timer.timerSpeed = 1.09F;
      Disabler ab = (Disabler)Client.INSTANCE.getModuleManager().getModule("Disabler");
      if (ab.isEnabled() && mc.thePlayer.onGround) {
         mc.thePlayer.jump();
      }

      if (this.getAllBlockCount() > 0) {
         if ((Boolean)this.keepYValue.getValue()) {
            if (this.posY > mc.thePlayer.posY || (double)mc.thePlayer.fallDistance > 1.5) {
               this.posY = mc.thePlayer.posY;
            }

            if (PlayerUtil.isOnGround(1.15) && !PlayerUtil.MovementInput() && !PlayerUtil.isOnGround(-2.0) && mc.gameSettings.keyBindJump.pressed && (Boolean)this.towerValue.getValue()) {
               this.posY = mc.thePlayer.posY;
            }
         } else {
            this.posY = mc.thePlayer.posY;
         }

         this.data = this.getBlockData(new BlockPos(mc.thePlayer.posX, this.posY - 1.0, mc.thePlayer.posZ)) == null ? this.getBlockData((new BlockPos(mc.thePlayer.posX, this.posY - 1.0, mc.thePlayer.posZ)).down()) : this.getBlockData(new BlockPos(mc.thePlayer.posX, this.posY - 1.0, mc.thePlayer.posZ));
         this.autoRefill(event);
         if (this.data != null) {
            this.slot = this.getBestBlockSlotHotBar() == -1 ? -1 : this.getBestBlockSlotHotBar() - 36;
            if (this.slot != -1) {
               if (event.isPre()) {
                  if (mc.thePlayer.onGround) {
                     this.offGroundTicks = 0;
                  } else {
                     ++this.offGroundTicks;
                  }

                  if (!(Boolean)this.sprintValue.getValue() && mc.thePlayer.isSprinting()) {
                     mc.thePlayer.setSprinting(false);
                  }

                  this.onRotation(event);
                  if (PlayerUtil.isOnGround(1.15) && !PlayerUtil.isOnGround(-2.0) && mc.gameSettings.keyBindJump.pressed && (Boolean)this.towerValue.getValue()) {
                     EntityPlayerSP var10000;
                     if (PlayerUtil.MovementInput()) {
                        if ((Boolean)this.towerMoveValue.getValue() && !(Boolean)this.keepYValue.getValue()) {
                           if (PlayerUtil.isOnGround(0.76) && !PlayerUtil.isOnGround(0.75) && mc.thePlayer.motionY > 0.23 && mc.thePlayer.motionY < 0.25) {
                              mc.thePlayer.motionY = (double)Math.round(mc.thePlayer.posY) - mc.thePlayer.posY;
                           }

                           if (PlayerUtil.isOnGround(1.0E-4)) {
                              mc.thePlayer.motionY = 0.41999998688698;
                              var10000 = mc.thePlayer;
                              var10000.motionX *= 0.9;
                              var10000 = mc.thePlayer;
                              var10000.motionZ *= 0.9;
                           } else if (mc.thePlayer.posY >= (double)Math.round(mc.thePlayer.posY) - 1.0E-4 && mc.thePlayer.posY <= (double)Math.round(mc.thePlayer.posY) + 1.0E-4) {
                              mc.thePlayer.motionY = 0.0;
                           }
                        }

                        if (mc.timer.timerSpeed == 1.0F + (((Number)this.boostValue.getValue()).floatValue() == 0.0F ? 0.0F : ((Number)this.boostValue.getValue()).floatValue() + 0.015555F)) {
                           mc.timer.timerSpeed = 1.0F;
                        }

                        this.towerTick = 0;
                     } else {
                        PlayerUtil.setSpeed(0.0);
                        if (mc.thePlayer.onGround) {
                           mc.thePlayer.motionY = 0.4000000059604645;
                        }

                        if (this.offGroundTicks == 3) {
                           var10000 = mc.thePlayer;
                           var10000.motionY -= 0.02;
                        }

                        if (PlayerUtil.isAirUnder(mc.thePlayer)) {
                           if (++this.towerTick <= ((Number)this.timesValue.getValue()).intValue()) {
                              if (this.towerTick > 0) {
                                 mc.timer.timerSpeed = 1.0F + (((Number)this.boostValue.getValue()).floatValue() == 0.0F ? 0.0F : ((Number)this.boostValue.getValue()).floatValue() + 0.015555F);
                              }
                           } else {
                              this.towerTick = 0;
                              if (mc.timer.timerSpeed == 1.0F + (((Number)this.boostValue.getValue()).floatValue() == 0.0F ? 0.0F : ((Number)this.boostValue.getValue()).floatValue() + 0.015555F)) {
                                 mc.timer.timerSpeed = 1.0F;
                              }
                           }
                        }
                     }
                  } else {
                     if (mc.timer.timerSpeed == 1.0F + (((Number)this.boostValue.getValue()).floatValue() == 0.0F ? 0.0F : ((Number)this.boostValue.getValue()).floatValue() + 0.015555F)) {
                        mc.timer.timerSpeed = 1.0F;
                     }

                     this.towerTick = 0;
                  }

                  if ((Boolean)this.eagleValue.getValue()) {
                     mc.gameSettings.keyBindSneak.pressed = PlayerUtil.isAirUnder(mc.thePlayer);
                  }
               }

               if (event.isPost()) {
                  if (this.slot == -1) {
                     this.timer.reset();
                     return;
                  }

                  if (!this.timer.hasPassed((long)((Number)this.delayValue.getValue()).intValue() * 100L)) {
                     return;
                  }

                  int last = mc.thePlayer.inventory.currentItem;
                  mc.thePlayer.inventory.currentItem = this.slot;
                  if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data.getBlockPos(), this.data.getEnumFacing(), this.getVec3ByBlockData(this.data).addVector(MathUtil.getRandom().nextDouble() / 5000.0, 0.0, MathUtil.getRandom().nextDouble() / 5000.0))) {
                     if ((Boolean)this.swingValue.getValue()) {
                        mc.thePlayer.swingItem();
                     } else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                     }
                  }

                  mc.thePlayer.inventory.currentItem = last;
                  this.timer.reset();
               }

            }
         }
      }
   }

   @EventTarget
   void onSafeWalk(SafeWalkEvent event) {
      event.setCancelled((Boolean)this.safeWalkValue.getValue() && mc.thePlayer.onGround);
   }

   private void onRotation(UpdateEvent event) {
      Block under = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)).getBlock();
      if (under == Blocks.air || !under.isFullBlock()) {
         Vec3 vec3 = this.getVec3ByBlockData(this.data);
         double x = vec3.xCoord - mc.thePlayer.posX;
         double y = mc.thePlayer.getEntityBoundingBox().minY + (double)mc.thePlayer.getEyeHeight() - vec3.yCoord;
         double z = vec3.zCoord - mc.thePlayer.posZ;
         double dist = (double)MathHelper.sqrt_double(x * x + z * z);
         float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
         float pitch = (float)(Math.atan2(y, dist) * 180.0 / Math.PI);
         if (yaw < 0.0F) {
            yaw += 360.0F;
         }

         rotation.setYaw(yaw);
         rotation.setPitch(pitch);
      }

      if (rotation.getYaw() != 999.0F) {
         event.setYaw(rotation.getYaw());
      }

      if (rotation.getPitch() != 999.0F) {
         event.setPitch(rotation.getPitch());
      }

   }

   private boolean isValid(Item item) {
      if (!(item instanceof ItemBlock)) {
         return false;
      } else {
         ItemBlock iBlock = (ItemBlock)item;
         Block block = iBlock.getBlock();
         return !this.blacklisted.contains(block);
      }
   }

   private Vec3 getVec3ByBlockData(BlockData data) {
      BlockPos pos = data.getBlockPos();
      double rand = 0.5 + (PlayerUtil.MovementInput() ? (MathUtil.getRandom().nextBoolean() ? MathUtil.getRandom(-1.0E-12, -1.0E-8) : MathUtil.getRandom(1.0E-12, 1.0E-8)) : 0.0);
      double x = (double)pos.getX() + rand;
      double y = (double)pos.getY() + rand;
      double z = (double)pos.getZ() + rand;
      return new Vec3(x, y, z);
   }

   public int getAllBlockCount() {
      int blockCount = 0;

      for(int i = 0; i < 45; ++i) {
         blockCount += mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock ? mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize : 0;
      }

      return blockCount;
   }

   public int getHotBarBlockCount() {
      int blockCount = 0;

      for(int i = 36; i < 45; ++i) {
         blockCount += mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock ? mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize : 0;
      }

      return blockCount;
   }

   private void autoRefill(UpdateEvent event) {
      if (event.isPre()) {
         int bestInvSlot = this.getBestBlockSlotInventory();
         int bestHotbarSlot = this.getBestBlockSlotHotBar();
         int bestSlot = this.getBestBlockSlotHotBar() > 0 ? this.getBestBlockSlotHotBar() : this.getBestBlockSlotInventory();
         int spoofSlot = 42;
         if (bestHotbarSlot > 0 && bestInvSlot > 0 && mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(bestHotbarSlot).getStack().stackSize < mc.thePlayer.inventoryContainer.getSlot(bestInvSlot).getStack().stackSize) {
            bestSlot = bestInvSlot;
         }

         int a;
         if (this.getBestBlockSlotHotBar() != -1) {
            for(a = 36; a < 45; ++a) {
               if (mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                  Item item = mc.thePlayer.inventoryContainer.getSlot(a).getStack().getItem();
                  if (item instanceof ItemBlock && this.isValid(item)) {
                     spoofSlot = a;
                     break;
                  }
               }
            }
         } else {
            for(a = 36; a < 45; ++a) {
               if (!mc.thePlayer.inventoryContainer.getSlot(a).getHasStack()) {
                  spoofSlot = a;
                  break;
               }
            }
         }

         if (mc.thePlayer.inventoryContainer.getSlot(spoofSlot).slotNumber != bestSlot) {
            InventoryUtil.swap(bestSlot, spoofSlot - 36);
            mc.playerController.updateController();
         }

      }
   }

   private int getBestBlockSlotHotBar() {
      int slot = -1;
      int size = 0;

      for(int i = 36; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (item instanceof ItemBlock && this.isValid(item) && is.stackSize > size) {
               size = is.stackSize;
               slot = i;
            }
         }
      }

      return slot;
   }

   private int getBestBlockSlotInventory() {
      int slot = -1;
      int size = 0;

      for(int i = 9; i < 36; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (item instanceof ItemBlock && this.isValid(item) && is.stackSize > size) {
               size = is.stackSize;
               slot = i;
            }
         }
      }

      return slot;
   }

   private BlockData getBlockData(BlockPos pos) {
      if (this.isPosValid(pos.add(0, -1, 0))) {
         return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
      } else if (this.isPosValid(pos.add(-1, 0, 0))) {
         return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
      } else if (this.isPosValid(pos.add(1, 0, 0))) {
         return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
      } else if (this.isPosValid(pos.add(0, 0, 1))) {
         return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
      } else if (this.isPosValid(pos.add(0, 0, -1))) {
         return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
      } else {
         BlockPos pos1 = pos.add(-1, 0, 0);
         if (this.isPosValid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
         } else if (this.isPosValid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
         } else if (this.isPosValid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
         } else if (this.isPosValid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
         } else if (this.isPosValid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
         } else {
            BlockPos pos2 = pos.add(1, 0, 0);
            if (this.isPosValid(pos2.add(0, -1, 0))) {
               return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
            } else if (this.isPosValid(pos2.add(-1, 0, 0))) {
               return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
            } else if (this.isPosValid(pos2.add(1, 0, 0))) {
               return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
            } else if (this.isPosValid(pos2.add(0, 0, 1))) {
               return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
            } else if (this.isPosValid(pos2.add(0, 0, -1))) {
               return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
            } else {
               BlockPos pos3 = pos.add(0, 0, 1);
               if (this.isPosValid(pos3.add(0, -1, 0))) {
                  return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
               } else if (this.isPosValid(pos3.add(-1, 0, 0))) {
                  return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
               } else if (this.isPosValid(pos3.add(1, 0, 0))) {
                  return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
               } else if (this.isPosValid(pos3.add(0, 0, 1))) {
                  return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
               } else if (this.isPosValid(pos3.add(0, 0, -1))) {
                  return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
               } else {
                  BlockPos pos4 = pos.add(0, 0, -1);
                  if (this.isPosValid(pos4.add(0, -1, 0))) {
                     return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
                  } else if (this.isPosValid(pos4.add(-1, 0, 0))) {
                     return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
                  } else if (this.isPosValid(pos4.add(1, 0, 0))) {
                     return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
                  } else if (this.isPosValid(pos4.add(0, 0, 1))) {
                     return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
                  } else if (this.isPosValid(pos4.add(0, 0, -1))) {
                     return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
                  } else {
                     BlockPos pos19 = pos.add(-2, 0, 0);
                     if (this.isPosValid(pos1.add(0, -1, 0))) {
                        return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
                     } else if (this.isPosValid(pos1.add(-1, 0, 0))) {
                        return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
                     } else if (this.isPosValid(pos1.add(1, 0, 0))) {
                        return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
                     } else if (this.isPosValid(pos1.add(0, 0, 1))) {
                        return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
                     } else if (this.isPosValid(pos1.add(0, 0, -1))) {
                        return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
                     } else {
                        BlockPos pos29 = pos.add(2, 0, 0);
                        if (this.isPosValid(pos2.add(0, -1, 0))) {
                           return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
                        } else if (this.isPosValid(pos2.add(-1, 0, 0))) {
                           return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
                        } else if (this.isPosValid(pos2.add(1, 0, 0))) {
                           return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
                        } else if (this.isPosValid(pos2.add(0, 0, 1))) {
                           return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
                        } else if (this.isPosValid(pos2.add(0, 0, -1))) {
                           return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
                        } else {
                           BlockPos pos39 = pos.add(0, 0, 2);
                           if (this.isPosValid(pos3.add(0, -1, 0))) {
                              return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
                           } else if (this.isPosValid(pos3.add(-1, 0, 0))) {
                              return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
                           } else if (this.isPosValid(pos3.add(1, 0, 0))) {
                              return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
                           } else if (this.isPosValid(pos3.add(0, 0, 1))) {
                              return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
                           } else if (this.isPosValid(pos3.add(0, 0, -1))) {
                              return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
                           } else {
                              BlockPos pos49 = pos.add(0, 0, -2);
                              if (this.isPosValid(pos4.add(0, -1, 0))) {
                                 return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
                              } else if (this.isPosValid(pos4.add(-1, 0, 0))) {
                                 return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
                              } else if (this.isPosValid(pos4.add(1, 0, 0))) {
                                 return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
                              } else if (this.isPosValid(pos4.add(0, 0, 1))) {
                                 return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
                              } else if (this.isPosValid(pos4.add(0, 0, -1))) {
                                 return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
                              } else {
                                 BlockPos pos5 = pos.add(0, -1, 0);
                                 if (this.isPosValid(pos5.add(0, -1, 0))) {
                                    return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
                                 } else if (this.isPosValid(pos5.add(-1, 0, 0))) {
                                    return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
                                 } else if (this.isPosValid(pos5.add(1, 0, 0))) {
                                    return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
                                 } else if (this.isPosValid(pos5.add(0, 0, 1))) {
                                    return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
                                 } else if (this.isPosValid(pos5.add(0, 0, -1))) {
                                    return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
                                 } else {
                                    BlockPos pos6 = pos5.add(1, 0, 0);
                                    if (this.isPosValid(pos6.add(0, -1, 0))) {
                                       return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
                                    } else if (this.isPosValid(pos6.add(-1, 0, 0))) {
                                       return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
                                    } else if (this.isPosValid(pos6.add(1, 0, 0))) {
                                       return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
                                    } else if (this.isPosValid(pos6.add(0, 0, 1))) {
                                       return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
                                    } else if (this.isPosValid(pos6.add(0, 0, -1))) {
                                       return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
                                    } else {
                                       BlockPos pos7 = pos5.add(-1, 0, 0);
                                       if (this.isPosValid(pos7.add(0, -1, 0))) {
                                          return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
                                       } else if (this.isPosValid(pos7.add(-1, 0, 0))) {
                                          return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
                                       } else if (this.isPosValid(pos7.add(1, 0, 0))) {
                                          return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
                                       } else if (this.isPosValid(pos7.add(0, 0, 1))) {
                                          return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
                                       } else if (this.isPosValid(pos7.add(0, 0, -1))) {
                                          return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
                                       } else {
                                          BlockPos pos8 = pos5.add(0, 0, 1);
                                          if (this.isPosValid(pos8.add(0, -1, 0))) {
                                             return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
                                          } else if (this.isPosValid(pos8.add(-1, 0, 0))) {
                                             return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
                                          } else if (this.isPosValid(pos8.add(1, 0, 0))) {
                                             return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
                                          } else if (this.isPosValid(pos8.add(0, 0, 1))) {
                                             return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
                                          } else if (this.isPosValid(pos8.add(0, 0, -1))) {
                                             return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
                                          } else {
                                             BlockPos pos9 = pos5.add(0, 0, -1);
                                             if (this.isPosValid(pos9.add(0, -1, 0))) {
                                                return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
                                             } else if (this.isPosValid(pos9.add(-1, 0, 0))) {
                                                return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
                                             } else if (this.isPosValid(pos9.add(1, 0, 0))) {
                                                return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
                                             } else if (this.isPosValid(pos9.add(0, 0, 1))) {
                                                return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
                                             } else {
                                                return this.isPosValid(pos9.add(0, 0, -1)) ? new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH) : null;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isPosValid(BlockPos pos) {
      Block block = mc.theWorld.getBlockState(pos).getBlock();
      return (block.getMaterial().isSolid() || !block.isTranslucent() || block.isVisuallyOpaque() || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
   }

   public static boolean isRotating() {
      return rotation.getYaw() != 999.0F || rotation.getPitch() != 999.0F;
   }

   private static class BlockData {
      private Vec3 vec;
      private final BlockPos pos;
      private final EnumFacing facing;

      public BlockData(BlockPos pos, EnumFacing facing) {
         this.pos = pos;
         this.facing = facing;
      }

      public Vec3 getVec() {
         return this.vec;
      }

      public void setVec(Vec3 vec) {
         this.vec = vec;
      }

      public BlockPos getBlockPos() {
         return this.pos;
      }

      public EnumFacing getEnumFacing() {
         return this.facing;
      }
   }
}
