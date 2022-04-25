package me.bigg.module.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.MoveEvent;
import me.bigg.event.PacketEvent;
import me.bigg.event.TickEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.module.impl.misc.scaffoldUtils.BlockUtils;
import me.bigg.module.impl.misc.scaffoldUtils.InventoryUtils;
import me.bigg.module.impl.misc.scaffoldUtils.Rotation;
import me.bigg.module.impl.misc.scaffoldUtils.RotationUtil;
import me.bigg.util.PlayerUtil;
import me.bigg.util.TimerUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Scaffold extends Module {
   private final EnumValue modeValue = new EnumValue("Mode", "Bypass modes", Scaffold.AuraMode.values());
   private NumValue placedelay = new NumValue("Place Delay", "Place Delay", 0.0, 0.0, 1000.0, 25.0);
   private NumValue length = new NumValue("Keep Rotation Length", "Keep Rotation Length", 0.0, 0.0, 20.0, 1.0);
   private NumValue expandSize = new NumValue("Expand Size", "Expand Size", 0.0, 0.0, 5.0, 1.0);
   private NumValue speedmodifier = new NumValue("Speed Modifier", "Speed Modifier", 0.1, 0.1, 2.0, 0.1);
   private NumValue timer = new NumValue("Timer", "Timer", 1.0, 0.5, 2.0, 0.1);
   private NumValue towertimer = new NumValue("TowerTimer", "TowerTimer", 1.0, 0.5, 2.0, 0.1);
   public static BoolValue noSprint = new BoolValue("noSprint", "noSprint", true);
   private BoolValue safewalk = new BoolValue("Safewalk", "Safewalk", true);
   private BoolValue keep = new BoolValue("Keep", "Keep", true);
   private BoolValue autoblock = new BoolValue("Auto Block", "Auto Block", true);
   private BoolValue stay_autoblock = new BoolValue("Stay Auto Block", "Stay Auto Block", true);
   private BoolValue keep_rot = new BoolValue("Keep Rotations", "Keep Rotations", true);
   private BoolValue rotations = new BoolValue("Rotations", "Rotations", true);
   private BoolValue search = new BoolValue("Search", "Search", true);
   private BoolValue swing = new BoolValue("Swing", "Swing", true);
   private BoolValue placeabledelay = new BoolValue("Place Able Delay", "Place Able Delay", true);
   private PlaceInfo targetPlace;
   private int launchY;
   private Rotation lockRotation;
   private int slot;
   private int oldSlot;
   private long delay;
   private double jumpGround = 0.0;
   private TimerUtil delayTimer = new TimerUtil();
   private int offGroundTicks;

   public Scaffold() {
      super("ScaffoldA", "Auto attack entities around you", Category.Misc);
      this.addValues(new Value[]{this.modeValue, this.placedelay, this.length, this.expandSize, this.speedmodifier, this.keep, this.autoblock, this.stay_autoblock, this.keep_rot, this.rotations, this.search, this.swing, this.placeabledelay, this.timer, this.towertimer, this.safewalk, noSprint});
   }

   public void onEnable() {
      if (mc.thePlayer != null) {
         this.oldSlot = mc.thePlayer.inventory.currentItem;
         this.launchY = (int)mc.thePlayer.posY;
      }
   }

   public void onDisable() {
      if (mc.thePlayer != null) {
         this.lockRotation = null;
         mc.timer.timerSpeed = 1.0F;
         if (this.slot != mc.thePlayer.inventory.currentItem) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
         }

         mc.thePlayer.inventory.currentItem = this.oldSlot;
      }
   }

   @EventTarget
   public void onTick(TickEvent event) {
      this.setLabel("NCP");
      RotationUtil.onTick(event);
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.thePlayer != null || event.isOutGoing()) {
         Packet packet = event.getPacket();
         if (packet instanceof C09PacketHeldItemChange) {
            C09PacketHeldItemChange packetHeldItemChange = (C09PacketHeldItemChange)packet;
            this.slot = packetHeldItemChange.getSlotId();
         }

      }
   }

   private void move() {
      if (mc.thePlayer.onGround) {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
         mc.thePlayer.motionY = 0.42;
         this.jumpGround = mc.thePlayer.posY;
      }

      if (mc.thePlayer.posY > this.jumpGround + 0.99) {
         mc.thePlayer.setPosition(mc.thePlayer.posX, (double)((int)mc.thePlayer.posY), mc.thePlayer.posZ);
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
         mc.thePlayer.motionY = 0.42;
         this.jumpGround = mc.thePlayer.posY;
      }

   }

   public int getBlockCount() {
      int blockCount = 0;

      for(int i = 0; i < 45; ++i) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if (is.getItem() instanceof ItemBlock) {
               blockCount += is.stackSize;
            }
         }
      }

      return blockCount;
   }

   public int grabBlockSlot() {
      for(int i = 0; i < 9; ++i) {
         ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
         if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize > 0) {
            return i;
         }
      }

      return -1;
   }

   @EventTarget
   public void OneTimeTask(MoveEvent event) {
      if ((Boolean)this.safewalk.getValue()) {
         double x = event.getX();
         double y = event.getY();
         double z = event.getZ();
         if (mc.thePlayer.onGround) {
            double increment = 0.05;

            while(x != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(x, -1.0, 0.0)).isEmpty()) {
               if (x < increment && x >= -increment) {
                  x = 0.0;
               } else if (x > 0.0) {
                  x -= increment;
               } else {
                  x += increment;
               }
            }

            while(z != 0.0 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -1.0, z)).isEmpty()) {
               if (z < increment && z >= -increment) {
                  z = 0.0;
               } else if (z > 0.0) {
                  z -= increment;
               } else {
                  z += increment;
               }
            }

            label68:
            while(true) {
               while(true) {
                  if (x == 0.0 || z == 0.0 || !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(x, -1.0, z)).isEmpty()) {
                     break label68;
                  }

                  if (x < increment && x >= -increment) {
                     x = 0.0;
                  } else if (x > 0.0) {
                     x -= increment;
                  } else {
                     x += increment;
                  }

                  if (z < increment && z >= -increment) {
                     z = 0.0;
                  } else if (z > 0.0) {
                     z -= increment;
                  } else {
                     z += increment;
                  }
               }
            }
         }

         event.setX(x);
         event.setY(y);
         event.setZ(z);
      }

   }

   @EventTarget
   private void onMotionUpdate(UpdateEvent e) {
      if (e.isPre()) {
         if (mc.thePlayer.onGround) {
            this.offGroundTicks = 0;
         } else {
            ++this.offGroundTicks;
         }

         if ((Boolean)noSprint.getValue()) {
            mc.thePlayer.setSprinting(false);
         }

         if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            PlayerUtil.setSpeed(0.06);
         }

         label55: {
            if ((Boolean)this.autoblock.getValue()) {
               if (InventoryUtils.findAutoBlockBlock() != -1) {
                  break label55;
               }
            } else if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
               break label55;
            }

            return;
         }

         this.findBlock(this.modeValue.getValue() == Scaffold.AuraMode.Expand);
         if ((Boolean)this.rotations.getValue() && (Boolean)this.keep_rot.getValue() && this.lockRotation != null) {
            RotationUtil.setTargetRotation(this.lockRotation);
            e.setYaw(RotationUtil.targetRotation.getYaw());
            e.setPitch(RotationUtil.targetRotation.getPitch());
         }

         if (mc.gameSettings.keyBindJump.isKeyDown() && !PlayerUtil.MovementInput() && BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ)) instanceof BlockAir) {
            mc.timer.timerSpeed = ((Double)this.towertimer.getValue()).floatValue();
         } else if (mc.thePlayer.onGround) {
            mc.timer.timerSpeed = 1.04F;
         } else {
            mc.timer.timerSpeed = 1.0F;
         }

      }
   }

   @EventTarget
   private void onMotionUpdatae(UpdateEvent e) {
      if (e.isPost()) {
         if (this.grabBlockSlot() == -1 && this.getBlockCount() != 0) {
            label43:
            for(int i = 9; i < 36; ++i) {
               if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                  ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                  if (is.getItem() instanceof ItemBlock) {
                     for(int i2 = 36; i2 < 45; ++i2) {
                        if (!mc.thePlayer.inventoryContainer.getSlot(i2).getHasStack()) {
                           mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                           break label43;
                        }
                     }

                     mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 8, 2, mc.thePlayer);
                     break;
                  }
               }
            }
         }

         this.place();
         if (this.targetPlace == null && (Boolean)this.placeabledelay.getValue()) {
            this.delayTimer.reset();
         }

      }
   }

   private void findBlock(boolean expand) {
      boolean canTower = mc.gameSettings.keyBindJump.pressed && !PlayerUtil.MovementInput() && BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ)) instanceof BlockAir;
      BlockPos blockPosition = mc.thePlayer.posY == (double)((int)mc.thePlayer.posY) + 0.5 ? new BlockPos(mc.thePlayer) : (new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)).down();
      if (expand || BlockUtils.isReplaceable(blockPosition) && !this.search(blockPosition, true)) {
         int x;
         if (expand) {
            for(x = 0; x < ((Double)this.expandSize.getValue()).intValue(); ++x) {
               if (this.search(blockPosition.add(mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST ? -x : (mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST ? x : 0), 0, mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH ? -x : (mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH ? x : 0)), false)) {
                  return;
               }
            }
         } else if ((Boolean)this.search.getValue() && !canTower) {
            for(x = -1; x <= 1; ++x) {
               for(int z = -1; z <= 1; ++z) {
                  if (this.search(blockPosition.add(x, 0, z), true)) {
                     return;
                  }
               }
            }
         }

      }
   }

   private void place() {
      if (this.targetPlace == null) {
         if ((Boolean)this.placeabledelay.getValue()) {
            this.delayTimer.reset();
         }

      } else if (this.delayTimer.hasPassed(this.delay) && (!(Boolean)this.keep.getValue() || this.launchY == (int)mc.thePlayer.posY)) {
         int blockSlot = -1;
         ItemStack itemStack = mc.thePlayer.getHeldItem();
         if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
            if (!(Boolean)this.autoblock.getValue()) {
               return;
            }

            blockSlot = InventoryUtils.findAutoBlockBlock();
            if (blockSlot == -1) {
               return;
            }

            mc.thePlayer.inventory.currentItem = blockSlot - 36;
            itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
         }

         if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, this.targetPlace.getBlockPos(), this.targetPlace.getEnumFacing(), this.targetPlace.getVec3())) {
            this.delayTimer.reset();
            this.delay = ((Double)this.placedelay.getValue()).longValue();
            if (mc.thePlayer.onGround) {
               float modifier = ((Double)this.speedmodifier.getValue()).floatValue();
               EntityPlayerSP var10000 = mc.thePlayer;
               var10000.motionX *= (double)modifier;
               var10000 = mc.thePlayer;
               var10000.motionZ *= (double)modifier;
            }

            if ((Boolean)this.swing.getValue()) {
               mc.thePlayer.swingItem();
            } else {
               mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
            }
         }

         if (!(Boolean)this.stay_autoblock.getValue() && blockSlot >= 0) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
         }

         this.targetPlace = null;
      }
   }

   private boolean search(BlockPos blockPosition, boolean checks) {
      if (!BlockUtils.isReplaceable(blockPosition)) {
         return false;
      } else {
         Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + (double)mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
         PlaceRotation placeRotation = null;
         EnumFacing[] var8;
         int var7 = (var8 = EnumFacing.values()).length;

         for(int var6 = 0; var6 < var7; ++var6) {
            EnumFacing side = var8[var6];
            BlockPos neighbor = blockPosition.offset(side);
            if (BlockUtils.canBeClicked(neighbor)) {
               Vec3 dirVec = new Vec3(side.getDirectionVec());

               for(double xSearch = 0.1; xSearch < 0.9; xSearch += 0.1) {
                  for(double ySearch = 0.1; ySearch < 0.9; ySearch += 0.1) {
                     for(double zSearch = 0.1; zSearch < 0.9; zSearch += 0.1) {
                        Vec3 posVec = (new Vec3(blockPosition)).addVector(xSearch, ySearch, zSearch);
                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));
                        if (!checks || !(eyesPos.squareDistanceTo(hitVec) > 18.0) && !(distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec))) && mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) == null) {
                           double diffX = hitVec.xCoord - eyesPos.xCoord;
                           double diffY = hitVec.yCoord - eyesPos.yCoord;
                           double diffZ = hitVec.zCoord - eyesPos.zCoord;
                           double diffXZ = (double)MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
                           Rotation rotation = new Rotation(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)))));
                           Vec3 rotationVector = RotationUtil.getVectorForRotation(rotation);
                           Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4.0, rotationVector.yCoord * 4.0, rotationVector.zCoord * 4.0);
                           MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                           if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor) && (placeRotation == null || RotationUtil.getRotationDifference(rotation) < RotationUtil.getRotationDifference(placeRotation.getRotation()))) {
                              placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                           }
                        }
                     }
                  }
               }
            }
         }

         if (placeRotation == null) {
            return false;
         } else {
            if ((Boolean)this.rotations.getValue()) {
               RotationUtil.setTargetRotation(placeRotation.getRotation(), ((Double)this.length.getValue()).intValue());
               this.lockRotation = placeRotation.getRotation();
            }

            this.targetPlace = placeRotation.getPlaceInfo();
            return true;
         }
      }
   }

   private int getBlocksAmount() {
      int amount = 0;

      for(int i = 36; i < 45; ++i) {
         ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
         if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            amount += itemStack.stackSize;
         }
      }

      return amount;
   }

   static enum AuraMode {
      Normal,
      Expand;
   }

   class PlaceInfo {
      private BlockPos pos;
      private EnumFacing enumFacing;
      private Vec3 vec3;

      public PlaceInfo(BlockPos pos, EnumFacing enumFacing) {
         this.pos = pos;
         this.enumFacing = enumFacing;
         this.vec3 = new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
      }

      public PlaceInfo(BlockPos pos, EnumFacing enumFacing, Vec3 vec3) {
         this.pos = pos;
         this.enumFacing = enumFacing;
         this.vec3 = vec3;
      }

      public Vec3 getVec3() {
         return this.vec3;
      }

      public BlockPos getBlockPos() {
         return this.pos;
      }

      public EnumFacing getEnumFacing() {
         return this.enumFacing;
      }

      public PlaceInfo get(BlockPos blockpos) {
         if (BlockUtils.canBeClicked(blockpos.add(0, -1, 0))) {
            return Scaffold.this.new PlaceInfo(blockpos.add(0, -1, 0), EnumFacing.UP);
         } else if (BlockUtils.canBeClicked(blockpos.add(0, 0, 1))) {
            return Scaffold.this.new PlaceInfo(blockpos.add(0, 0, 1), EnumFacing.NORTH);
         } else if (BlockUtils.canBeClicked(blockpos.add(0, 0, -1))) {
            return Scaffold.this.new PlaceInfo(blockpos.add(0, 0, -1), EnumFacing.SOUTH);
         } else if (BlockUtils.canBeClicked(blockpos.add(-1, 0, 0))) {
            return Scaffold.this.new PlaceInfo(blockpos.add(-1, 0, 0), EnumFacing.EAST);
         } else {
            return BlockUtils.canBeClicked(blockpos.add(1, 0, 0)) ? Scaffold.this.new PlaceInfo(blockpos.add(1, 0, 0), EnumFacing.WEST) : null;
         }
      }
   }

   class PlaceRotation {
      private PlaceInfo info;
      private Rotation rot;

      public PlaceRotation(PlaceInfo info, Rotation rot) {
         this.info = info;
         this.rot = rot;
      }

      public PlaceInfo getPlaceInfo() {
         return this.info;
      }

      public Rotation getRotation() {
         return this.rot;
      }
   }
}
