package me.bigg.event;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BBBEvent extends EventCancellable {
   private BlockPos pos;
   private Block block;
   private AxisAlignedBB boundingBox;

   public BBBEvent(BlockPos pos, Block block, AxisAlignedBB boundingBox) {
      this.pos = pos;
      this.block = block;
      this.boundingBox = boundingBox;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setPos(BlockPos pos) {
      this.pos = pos;
   }

   public Block getBlock() {
      return this.block;
   }

   public void setBlock(Block block) {
      this.block = block;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   public void setBoundingBox(AxisAlignedBB boundingBox) {
      this.boundingBox = boundingBox;
   }
}
