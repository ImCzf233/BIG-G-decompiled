package me.bigg.module.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import me.bigg.event.PacketEvent;
import me.bigg.event.UpdateEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.value.EnumValue;
import me.bigg.value.Value;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class FastMine extends Module {
   private EnumValue modeValue = new EnumValue("Mode", "Fucking mode", FastMine.modeEnums.values());
   private BlockPos blockPos;
   private EnumFacing facing;
   private boolean digging;
   private float damage;

   public FastMine() {
      super("Fast Mine", "Mine faster", Category.Player);
      this.addValues(new Value[]{this.modeValue});
   }

   @EventTarget
   void onUpdate(UpdateEvent event) {
      if (event.isPost()) {
         if (this.modeValue.getMode() == FastMine.modeEnums.Legit) {
            mc.playerController.blockHitDelay = 0;
            if ((double)mc.playerController.curBlockDamageMP >= 0.65) {
               mc.playerController.curBlockDamageMP = 1.0F;
            }
         }

         if (this.modeValue.getMode() == FastMine.modeEnums.Packet) {
            mc.playerController.blockHitDelay = 0;
            if (this.digging && !mc.playerController.isInCreativeMode()) {
               Block block = mc.theWorld.getBlockState(this.blockPos).getBlock();
               this.damage = (float)((double)this.damage + (double)block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.blockPos) * 1.4);
               if (this.damage >= 1.0F) {
                  mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                  mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
                  this.damage = 0.0F;
                  this.digging = false;
               }
            }
         }
      }

   }

   @EventTarget
   void onPacket(PacketEvent event) {
      Packet p = event.getPacket();
      if (event.isOutGoing() && this.modeValue.getMode() == FastMine.modeEnums.Packet && p instanceof C07PacketPlayerDigging && !mc.playerController.isInCreativeMode()) {
         C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)p;
         if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            this.digging = true;
            this.blockPos = c07PacketPlayerDigging.getPosition();
            this.facing = c07PacketPlayerDigging.getFacing();
            this.damage = 0.0F;
         } else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
            this.digging = false;
            this.blockPos = null;
            this.facing = null;
         }
      }

   }

   private static enum modeEnums {
      Legit,
      Packet;
   }
}
