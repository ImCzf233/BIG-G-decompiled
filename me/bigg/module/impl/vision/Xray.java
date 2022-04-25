package me.bigg.module.impl.vision;

import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import me.bigg.event.BlockRenderSideEvent;
import me.bigg.event.RenderEvent;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.RenderUtil;
import me.bigg.value.BoolValue;
import me.bigg.value.EnumValue;
import me.bigg.value.NumValue;
import me.bigg.value.Value;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class Xray extends Module {
   public static final NumValue alpha = new NumValue("Alpha", "1", 150.0, 0.0, 255.0, 1.0);
   private static final NumValue range = new NumValue("Range", "1", 50.0, 0.0, 500.0, 1.0);
   private static final NumValue extremeRange = new NumValue("extremeRange", "1", 4.0, 0.0, 6.0, 1.0);
   public static BoolValue coal = new BoolValue("Coal", "e", true);
   public static BoolValue iron = new BoolValue("iron", "e", true);
   public static BoolValue gold = new BoolValue("gold", "e", true);
   public static BoolValue lapisLazuli = new BoolValue("lapisLazuli", "e", true);
   public static BoolValue diamond = new BoolValue("diamond", "e", true);
   public static BoolValue redStone = new BoolValue("redStone", "e", true);
   public static BoolValue emerald = new BoolValue("emerald", "e", false);
   public static BoolValue quartz = new BoolValue("quartz", "e", false);
   public static BoolValue water = new BoolValue("water", "e", false);
   public static BoolValue lava = new BoolValue("lava", "e", false);
   public static BoolValue tracer = new BoolValue("tracer", "e", false);
   public static BoolValue block = new BoolValue("block", "e", false);
   public static BoolValue cave = new BoolValue("cave", "e", false);
   public static BoolValue esp = new BoolValue("esp", "e", true);
   public static BoolValue extreme = new BoolValue("extreme", "e", true);
   private final EnumValue fakeMineralMode = new EnumValue("Mode", "e", Xray.modeEnums.values());
   public static final LinkedList antiXRayBlocks = new LinkedList();
   private final CopyOnWriteArrayList xRayBlocks = new CopyOnWriteArrayList();
   public static boolean isEnable = false;
   Block[] _extreme_var0;

   public Xray() {
      super("Xray", "Render cape", Category.Vision);
      this._extreme_var0 = new Block[]{Blocks.obsidian, Blocks.clay, Blocks.mossy_cobblestone, Blocks.diamond_ore, Blocks.redstone_ore, Blocks.iron_ore, Blocks.coal_ore, Blocks.lapis_ore, Blocks.gold_ore, Blocks.emerald_ore, Blocks.quartz_ore};
      this.addValues(new Value[]{this.fakeMineralMode, alpha, range, extremeRange, coal, gold, iron, lapisLazuli, diamond, redStone, emerald, quartz, water, lava, tracer, block, cave, esp, extreme});
   }

   public void onEnable() {
      if ((Boolean)extreme.getValue() && mc.thePlayer.posY <= 25.0) {
         this.doExtreme();
      }

      mc.renderGlobal.loadRenderers();
      int posX = (int)mc.thePlayer.posX;
      int posY = (int)mc.thePlayer.posY;
      int posZ = (int)mc.thePlayer.posZ;
      mc.renderGlobal.markBlockRangeForRenderUpdate(posX - 900, posY - 900, posZ - 900, posX + 900, posY + 900, posZ + 900);
      this.addAntiXRayBlocks();
      isEnable = true;
      super.onEnable();
   }

   public void onDisable() {
      mc.renderGlobal.loadRenderers();
      antiXRayBlocks.clear();
      this.xRayBlocks.clear();
      isEnable = false;
      super.onDisable();
   }

   @EventTarget
   public void onEventBlockRenderSide(BlockRenderSideEvent e) {
      Iterator var3 = antiXRayBlocks.iterator();

      while(true) {
         while(true) {
            int id;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               id = (Integer)var3.next();
            } while(Block.getIdFromBlock(e.getBlock()) != id);

            if (!(Boolean)cave.getValue()) {
               break;
            }

            if (this.fakeMineralMode.getValue() != Xray.modeEnums.Normal) {
               if (this.fakeMineralMode.getValue() != Xray.modeEnums.FoodByte || this._FoodByteMethod1(e.getBlockAccess(), e.getPos(), e.getMinY(), e.getMaxY(), e.getMinZ(), e.getMaxZ(), e.getMinX(), e.getMaxX())) {
                  break;
               }
            } else {
               boolean notCaveB = e.getSide() == EnumFacing.DOWN && e.getMinY() > 0.0 ? true : (e.getSide() == EnumFacing.UP && e.getMaxY() < 1.0 ? true : (e.getSide() == EnumFacing.NORTH && e.getMinZ() > 0.0 ? true : (e.getSide() == EnumFacing.SOUTH && e.getMaxZ() < 1.0 ? true : (e.getSide() == EnumFacing.WEST && e.getMinX() > 0.0 ? true : (e.getSide() == EnumFacing.EAST && e.getMaxX() < 1.0 ? true : !e.getBlockAccess().getBlockState(e.getPos()).getBlock().isOpaqueCube())))));
               boolean notCaveWater = false;
               BlockPos[] pos = new BlockPos[6];

               for(int i = 0; i < pos.length; ++i) {
                  switch (i) {
                     case 0:
                        pos[i] = e.getPos().up();
                        break;
                     case 1:
                        pos[i] = e.getPos().down();
                        break;
                     case 2:
                        pos[i] = e.getPos().south();
                        break;
                     case 3:
                        pos[i] = e.getPos().north();
                        break;
                     case 4:
                        pos[i] = e.getPos().east();
                        break;
                     case 5:
                        pos[i] = e.getPos().west();
                  }
               }

               BlockPos[] var10 = pos;
               int var9 = pos.length;

               for(int var8 = 0; var8 < var9; ++var8) {
                  BlockPos po = var10[var8];
                  Block currentBlock = mc.theWorld.getBlock(po);
                  if (currentBlock instanceof BlockAir || currentBlock instanceof BlockLiquid) {
                     notCaveWater = true;
                  }
               }

               if (Arrays.stream(pos).allMatch((pos1) -> {
                  return mc.theWorld.getBlock(pos1).getUnlocalizedName().equals(Blocks.bedrock.getUnlocalizedName());
               })) {
                  notCaveWater = true;
               }

               if (notCaveB || notCaveWater) {
                  break;
               }
            }
         }

         e.shouldRender = true;
         if (!(Boolean)esp.getValue() && !(Boolean)tracer.getValue()) {
            return;
         }

         float xDiff = (float)(mc.thePlayer.posX - (double)e.getPos().getX());
         float yDiff = 0.0F;
         float zDiff = (float)(mc.thePlayer.posZ - (double)e.getPos().getZ());
         float dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
         if (!(dis > ((Number)range.getValue()).floatValue())) {
            XRayBlock x = new XRayBlock((double)Math.round((float)e.getPos().offset(e.getSide(), -1).getX()), (double)Math.round((float)e.getPos().offset(e.getSide(), -1).getY()), (double)Math.round((float)e.getPos().offset(e.getSide(), -1).getZ()), e.getBlock());
            if (!this.xRayBlocks.contains(x)) {
               this.xRayBlocks.add(x);
            }
         }
      }
   }

   private boolean _FoodByteMethod1(IBlockAccess iBlockAccess, BlockPos blockPos, double minY, double maxY, double minZ, double maxZ, double minX, double maxX) {
      EnumFacing[] var18;
      int var17 = (var18 = EnumFacing.VALUES).length;

      for(int var16 = 0; var16 < var17; ++var16) {
         EnumFacing enumFacing2 = var18[var16];
         if (this._FoodByteMethod0(iBlockAccess, blockPos.offset(enumFacing2), enumFacing2, minY, maxY, minZ, maxZ, minX, maxX)) {
            return true;
         }
      }

      return false;
   }

   private boolean _FoodByteMethod0(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing, double minY, double maxY, double minZ, double maxZ, double minX, double maxX) {
      return enumFacing == EnumFacing.DOWN && minY > 0.0 || enumFacing == EnumFacing.UP && maxY < 1.0 || enumFacing == EnumFacing.NORTH && minZ > 0.0 || enumFacing == EnumFacing.SOUTH && maxZ < 1.0 || enumFacing == EnumFacing.WEST && minX > 0.0 || enumFacing == EnumFacing.EAST && maxX < 1.0 || !iBlockAccess.getBlockState(blockPos).getBlock().isOpaqueCube();
   }

   @EventTarget
   public void on3D(RenderEvent e) {
      Iterator var3 = this.xRayBlocks.iterator();

      while(true) {
         while(var3.hasNext()) {
            XRayBlock block = (XRayBlock)var3.next();
            BlockPos currentPos = new BlockPos(block.getX(), block.getY(), block.getZ());
            if (!(mc.theWorld.getBlock(currentPos) instanceof BlockOre)) {
               this.xRayBlocks.remove(block);
            } else {
               Color color;
               label108: {
                  label107: {
                     label106: {
                        label105: {
                           label104: {
                              label103: {
                                 color = new Color(12, 12, 12);
                                 String var6;
                                 switch ((var6 = block.getBlock().getUnlocalizedName()).hashCode()) {
                                    case -2092402634:
                                       if (!var6.equals("tile.oreRedstone")) {
                                          break label108;
                                       }
                                       break label104;
                                    case -1261180894:
                                       if (!var6.equals("tile.oreEmerald")) {
                                          break label108;
                                       }
                                       break;
                                    case -1104837919:
                                       if (!var6.equals("tile.blockRedstone")) {
                                          break label108;
                                       }
                                       break label104;
                                    case 276021944:
                                       if (!var6.equals("tile.blockLapis")) {
                                          break label108;
                                       }
                                       break label105;
                                    case 674320199:
                                       if (!var6.equals("tile.blockDiamond")) {
                                          break label108;
                                       }
                                       break label103;
                                    case 1037850675:
                                       if (var6.equals("tile.netherquartz")) {
                                          color = new Color(255, 255, 255);
                                       }
                                       break label108;
                                    case 1117146957:
                                       if (!var6.equals("tile.blockGold")) {
                                          break label108;
                                       }
                                       break label107;
                                    case 1117209525:
                                       if (!var6.equals("tile.blockIron")) {
                                          break label108;
                                       }
                                       break label106;
                                    case 1524356483:
                                       if (!var6.equals("tile.oreLapis")) {
                                          break label108;
                                       }
                                       break label105;
                                    case 1680170007:
                                       if (!var6.equals("tile.blockEmerald")) {
                                          break label108;
                                       }
                                       break;
                                    case 2027936594:
                                       if (!var6.equals("tile.oreDiamond")) {
                                          break label108;
                                       }
                                       break label103;
                                    case 2127247138:
                                       if (!var6.equals("tile.oreGold")) {
                                          break label108;
                                       }
                                       break label107;
                                    case 2127309706:
                                       if (!var6.equals("tile.oreIron")) {
                                          break label108;
                                       }
                                       break label106;
                                    default:
                                       break label108;
                                 }

                                 color = new Color(0, 255, 0);
                                 break label108;
                              }

                              color = new Color(0, 255, 255);
                              break label108;
                           }

                           color = new Color(16711680);
                           break label108;
                        }

                        color = new Color(255);
                        break label108;
                     }

                     color = new Color(210, 210, 210);
                     break label108;
                  }

                  color = new Color(16776960);
               }

               double posX;
               double posY;
               double posZ;
               if ((Boolean)esp.getValue()) {
                  posX = block.x - mc.getRenderManager().renderPosX;
                  posY = block.y - mc.getRenderManager().renderPosY;
                  posZ = block.z - mc.getRenderManager().renderPosZ;
                  double minX = !(block.getBlock() instanceof BlockStairs) && Block.getIdFromBlock(block.getBlock()) != 134 ? block.getBlock().getBlockBoundsMinX() : 0.0;
                  double minY = !(block.getBlock() instanceof BlockStairs) && Block.getIdFromBlock(block.getBlock()) != 134 ? block.getBlock().getBlockBoundsMinY() : 0.0;
                  double minZ = !(block.getBlock() instanceof BlockStairs) && Block.getIdFromBlock(block.getBlock()) != 134 ? block.getBlock().getBlockBoundsMinZ() : 0.0;
                  GL11.glPushMatrix();
                  GL11.glEnable(3042);
                  GL11.glBlendFunc(770, 771);
                  GL11.glDisable(3553);
                  GL11.glDisable(2929);
                  GL11.glDepthMask(false);
                  GL11.glLineWidth(1.0F);
                  GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.627451F);
                  RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(posX + minX, posY + minY, posZ + minZ, posX + block.getBlock().getBlockBoundsMaxX(), posY + block.getBlock().getBlockBoundsMaxY(), posZ + block.getBlock().getBlockBoundsMaxZ()));
                  GL11.glColor3f(1.0F, 1.0F, 1.0F);
                  GL11.glEnable(3553);
                  GL11.glEnable(2929);
                  GL11.glDepthMask(true);
                  GL11.glDisable(3042);
                  GL11.glPopMatrix();
               }

               if ((Boolean)tracer.getValue()) {
                  posX = block.x - mc.getRenderManager().renderPosX;
                  posY = block.y - mc.getRenderManager().renderPosY;
                  posZ = block.z - mc.getRenderManager().renderPosZ;
                  boolean oldBobbing = mc.gameSettings.viewBobbing;
                  mc.gameSettings.viewBobbing = false;
                  mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                  mc.gameSettings.viewBobbing = oldBobbing;
                  GL11.glPushMatrix();
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL11.glEnable(2848);
                  GL11.glDisable(3553);
                  GL11.glColor4f((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F);
                  GL11.glLineWidth(0.5F);
                  GL11.glBegin(1);
                  GL11.glVertex3d(0.0, (double)mc.thePlayer.getEyeHeight(), 0.0);
                  GL11.glVertex3d(posX, posY, posZ);
                  GL11.glEnd();
                  GL11.glDisable(2848);
                  GL11.glEnable(3553);
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL11.glPopMatrix();
               }
            }
         }

         return;
      }
   }

   public void doExtreme() {
      int var1 = ((Number)extremeRange.getValue()).intValue();

      for(int var2 = -var1; var2 < var1; ++var2) {
         for(int var3 = var1; var3 > -var1; --var3) {
            for(int var4 = -var1; var4 < var1; ++var4) {
               int var5 = (int)Math.floor(mc.thePlayer.posX) + var2;
               int var6 = (int)Math.floor(mc.thePlayer.posY) + var3;
               int var7 = (int)Math.floor(mc.thePlayer.posZ) + var4;
               if (mc.thePlayer.getDistanceSq(mc.thePlayer.posX + (double)var2, mc.thePlayer.posY + (double)var3, mc.thePlayer.posZ + (double)var4) <= 16.0) {
                  Block var8 = mc.theWorld.getBlockState(new BlockPos(var5, var6, var7)).getBlock();
                  boolean var9 = false;
                  Block[] var10 = this._extreme_var0;
                  Block[] var14 = var10;
                  int var13 = var10.length;

                  for(int var12 = 0; var12 < var13; ++var12) {
                     Block var13 = var14[var12];
                     if (var8.equals(var13)) {
                        var9 = true;
                        break;
                     }
                  }

                  var9 = var9 && (var8.getBlockHardness(mc.theWorld, BlockPos.ORIGIN) != -1.0F || mc.playerController.isInCreativeMode());
                  boolean dont = false;
                  Iterator var18 = this.xRayBlocks.iterator();

                  while(var18.hasNext()) {
                     XRayBlock xRayBlock = (XRayBlock)var18.next();
                     if (xRayBlock.samePos(new BlockPos(var5, var6, var7))) {
                        dont = true;
                        break;
                     }
                  }

                  if (var9 && !dont) {
                     BlockPos pos = new BlockPos(var5, var6, var7);
                     mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, EnumFacing.UP));
                     this.xRayBlocks.add(new XRayBlock((double)var5, (double)var6, (double)var7, mc.theWorld.getBlock(pos)));
                  }
               }
            }
         }
      }

   }

   private void addAntiXRayBlocks() {
      if ((Boolean)coal.getValue()) {
         antiXRayBlocks.add(16);
         if ((Boolean)block.getValue()) {
            antiXRayBlocks.add(173);
         }
      }

      if ((Boolean)iron.getValue()) {
         antiXRayBlocks.add(15);
         if ((Boolean)block.getValue()) {
            antiXRayBlocks.add(42);
         }
      }

      if ((Boolean)gold.getValue()) {
         antiXRayBlocks.add(14);
         if ((Boolean)block.getValue()) {
            antiXRayBlocks.add(41);
         }
      }

      if ((Boolean)lapisLazuli.getValue()) {
         antiXRayBlocks.add(21);
         if ((Boolean)block.getValue()) {
            antiXRayBlocks.add(22);
         }
      }

      if ((Boolean)diamond.getValue()) {
         antiXRayBlocks.add(56);
         if ((Boolean)block.getValue()) {
            antiXRayBlocks.add(57);
         }
      }

      if ((Boolean)redStone.getValue()) {
         antiXRayBlocks.add(73);
         antiXRayBlocks.add(74);
         if ((Boolean)block.getValue()) {
            antiXRayBlocks.add(152);
         }
      }

      if ((Boolean)emerald.getValue()) {
         antiXRayBlocks.add(129);
         if ((Boolean)block.getValue()) {
            antiXRayBlocks.add(133);
         }
      }

      if ((Boolean)quartz.getValue()) {
         antiXRayBlocks.add(153);
      }

      if ((Boolean)water.getValue()) {
         antiXRayBlocks.add(8);
         antiXRayBlocks.add(9);
      }

      if ((Boolean)lava.getValue()) {
         antiXRayBlocks.add(10);
         antiXRayBlocks.add(11);
      }

   }

   private static class XRayBlock {
      private final double x;
      private final double y;
      private final double z;
      private final Block block;

      public XRayBlock(double x, double y, double z, Block block) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.block = block;
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public double getZ() {
         return this.z;
      }

      public Block getBlock() {
         return this.block;
      }

      public boolean samePos(BlockPos blockPos) {
         return (int)this.x == blockPos.getX() && (int)this.y == blockPos.getY() && this.z == (double)blockPos.getZ();
      }
   }

   private static enum modeEnums {
      Normal,
      FoodByte;
   }
}
