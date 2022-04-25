package me.bigg.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class PathUtil {
   private static Minecraft mc = Minecraft.getMinecraft();
   private Vec3 startVec3;
   private Vec3 endVec3;
   private ArrayList path = new ArrayList();
   private ArrayList hubs = new ArrayList();
   private ArrayList hubsToWork = new ArrayList();
   private double minDistanceSquared = 9.0;
   private boolean nearest = true;
   private static Vec3[] flatCardinalDirections = new Vec3[]{new Vec3(1.0, 0.0, 0.0), new Vec3(-1.0, 0.0, 0.0), new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 0.0, -1.0)};

   public PathUtil(Vec3 startVec3, Vec3 endVec3) {
      this.startVec3 = startVec3.addVector(0.0, 0.0, 0.0).floor();
      this.endVec3 = endVec3.addVector(0.0, 0.0, 0.0).floor();
   }

   public ArrayList getPath() {
      return this.path;
   }

   public void compute() {
      this.compute(1000, 4);
   }

   public void compute(int loops, int depth) {
      this.path.clear();
      this.hubsToWork.clear();
      ArrayList initPath = new ArrayList();
      initPath.add(this.startVec3);
      this.hubsToWork.add(new Hub(this.startVec3, (Hub)null, initPath, this.startVec3.squareDistanceTo(this.endVec3), 0.0, 0.0));

      label57:
      for(int i = 0; i < loops; ++i) {
         this.hubsToWork.sort(new CompareHub());
         int j = 0;
         if (this.hubsToWork.size() == 0) {
            break;
         }

         Iterator var7 = (new ArrayList(this.hubsToWork)).iterator();

         while(var7.hasNext()) {
            Hub hub = (Hub)var7.next();
            ++j;
            if (j > depth) {
               break;
            }

            this.hubsToWork.remove(hub);
            this.hubs.add(hub);
            Vec3[] var11;
            int var10 = (var11 = flatCardinalDirections).length;

            Vec3 loc1;
            for(int var9 = 0; var9 < var10; ++var9) {
               loc1 = var11[var9];
               Vec3 loc = hub.getLoc().add(loc1).floor();
               if (checkPositionValidity(loc, false) && this.addHub(hub, loc, 0.0)) {
                  break label57;
               }
            }

            loc1 = hub.getLoc().addVector(0.0, 1.0, 0.0).floor();
            if (checkPositionValidity(loc1, false) && this.addHub(hub, loc1, 0.0)) {
               break label57;
            }

            Vec3 loc2 = hub.getLoc().addVector(0.0, -1.0, 0.0).floor();
            if (checkPositionValidity(loc2, false) && this.addHub(hub, loc2, 0.0)) {
               break label57;
            }
         }
      }

      if (this.nearest) {
         Collections.sort(this.hubs, new CompareHub());
         this.path = ((Hub)this.hubs.get(0)).getPath();
      }

   }

   public static boolean checkPositionValidity(net.minecraft.util.Vec3 vec3) {
      BlockPos pos = new BlockPos(vec3);
      return !isBlockSolid(pos) && !isBlockSolid(pos.add(0, 1, 0)) ? isSafeToWalkOn(pos.add(0, -1, 0)) : false;
   }

   public static boolean checkPositionValidity(Vec3 loc, boolean checkGround) {
      return checkPositionValidity((int)loc.getX(), (int)loc.getY(), (int)loc.getZ(), checkGround);
   }

   public static boolean checkPositionValidity(int x, int y, int z, boolean checkGround) {
      BlockPos block1 = new BlockPos(x, y, z);
      BlockPos block2 = new BlockPos(x, y + 1, z);
      BlockPos block3 = new BlockPos(x, y - 1, z);
      return !isBlockSolid(block1) && !isBlockSolid(block2) && (isBlockSolid(block3) || !checkGround) && isSafeToWalkOn(block3);
   }

   private static boolean isBlockSolid(BlockPos block) {
      return mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock().isBlockNormalCube() || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockSlab || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockStairs || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockCactus || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockChest || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockEnderChest || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockSkull || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPane || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockFence || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockWall || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockGlass || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPistonBase || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPistonExtension || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockPistonMoving || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockStainedGlass || mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockTrapDoor;
   }

   private static boolean isSafeToWalkOn(BlockPos block) {
      return !(mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockFence) && !(mc.theWorld.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ())).getBlock() instanceof BlockWall);
   }

   public Hub isHubExisting(Vec3 loc) {
      Iterator var3 = this.hubs.iterator();

      Hub hub;
      do {
         if (!var3.hasNext()) {
            var3 = this.hubsToWork.iterator();

            do {
               if (!var3.hasNext()) {
                  return null;
               }

               hub = (Hub)var3.next();
            } while(hub.getLoc().getX() != loc.getX() || hub.getLoc().getY() != loc.getY() || hub.getLoc().getZ() != loc.getZ());

            return hub;
         }

         hub = (Hub)var3.next();
      } while(hub.getLoc().getX() != loc.getX() || hub.getLoc().getY() != loc.getY() || hub.getLoc().getZ() != loc.getZ());

      return hub;
   }

   public boolean addHub(Hub parent, Vec3 loc, double cost) {
      Hub existingHub = this.isHubExisting(loc);
      double totalCost = cost;
      if (parent != null) {
         totalCost = cost + parent.getTotalCost();
      }

      ArrayList path;
      if (existingHub == null) {
         if (loc.getX() == this.endVec3.getX() && loc.getY() == this.endVec3.getY() && loc.getZ() == this.endVec3.getZ() || this.minDistanceSquared != 0.0 && loc.squareDistanceTo(this.endVec3) <= this.minDistanceSquared) {
            this.path.clear();
            this.path = parent.getPath();
            this.path.add(loc);
            return true;
         }

         path = new ArrayList(parent.getPath());
         path.add(loc);
         this.hubsToWork.add(new Hub(loc, parent, path, loc.squareDistanceTo(this.endVec3), cost, totalCost));
      } else if (existingHub.getCost() > cost) {
         path = new ArrayList(parent.getPath());
         path.add(loc);
         existingHub.setLoc(loc);
         existingHub.setParent(parent);
         existingHub.setPath(path);
         existingHub.setSquareDistanceToFromTarget(loc.squareDistanceTo(this.endVec3));
         existingHub.setCost(cost);
         existingHub.setTotalCost(totalCost);
      }

      return false;
   }

   public static ArrayList computePath(Vec3 topFrom, Vec3 to) {
      if (!canPassThrow(new BlockPos(topFrom.mc()))) {
         topFrom = topFrom.addVector(0.0, 1.0, 0.0);
      }

      PathUtil pathfinder = new PathUtil(topFrom, to);
      pathfinder.compute();
      int i = 0;
      Vec3 lastLoc = null;
      Vec3 lastDashLoc = null;
      ArrayList path = new ArrayList();
      ArrayList pathFinderPath = pathfinder.getPath();

      for(Iterator var9 = pathFinderPath.iterator(); var9.hasNext(); ++i) {
         Vec3 pathElm = (Vec3)var9.next();
         if (i != 0 && i != pathFinderPath.size() - 1) {
            boolean canContinue = true;
            if (pathElm.squareDistanceTo(lastDashLoc) > 25.0) {
               canContinue = false;
            } else {
               double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
               double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
               double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
               double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
               double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
               double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());

               label54:
               for(int x = (int)smallX; (double)x <= bigX; ++x) {
                  for(int y = (int)smallY; (double)y <= bigY; ++y) {
                     for(int z = (int)smallZ; (double)z <= bigZ; ++z) {
                        if (!checkPositionValidity(x, y, z, false)) {
                           canContinue = false;
                           break label54;
                        }
                     }
                  }
               }
            }

            if (!canContinue) {
               path.add(lastLoc.addVector(0.5, 0.0, 0.5));
               lastDashLoc = lastLoc;
            }
         } else {
            if (lastLoc != null) {
               path.add(lastLoc.addVector(0.5, 0.0, 0.5));
            }

            path.add(pathElm.addVector(0.5, 0.0, 0.5));
            lastDashLoc = pathElm;
         }

         lastLoc = pathElm;
      }

      return path;
   }

   private static boolean canPassThrow(BlockPos pos) {
      Block block = mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
      return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
   }

   public class CompareHub implements Comparator {
      public int compare(Hub o1, Hub o2) {
         return (int)(o1.getSquareDistanceToFromTarget() + o1.getTotalCost() - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost()));
      }
   }

   private static class Hub {
      private Vec3 loc = null;
      private Hub parent = null;
      private ArrayList path;
      private double squareDistanceToFromTarget;
      private double cost;
      private double totalCost;

      public Hub(Vec3 loc, Hub parent, ArrayList path, double squareDistanceToFromTarget, double cost, double totalCost) {
         this.loc = loc;
         this.parent = parent;
         this.path = path;
         this.squareDistanceToFromTarget = squareDistanceToFromTarget;
         this.cost = cost;
         this.totalCost = totalCost;
      }

      public Vec3 getLoc() {
         return this.loc;
      }

      public Hub getParent() {
         return this.parent;
      }

      public ArrayList getPath() {
         return this.path;
      }

      public double getSquareDistanceToFromTarget() {
         return this.squareDistanceToFromTarget;
      }

      public double getCost() {
         return this.cost;
      }

      public void setLoc(Vec3 loc) {
         this.loc = loc;
      }

      public void setParent(Hub parent) {
         this.parent = parent;
      }

      public void setPath(ArrayList path) {
         this.path = path;
      }

      public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
         this.squareDistanceToFromTarget = squareDistanceToFromTarget;
      }

      public void setCost(double cost) {
         this.cost = cost;
      }

      public double getTotalCost() {
         return this.totalCost;
      }

      public void setTotalCost(double totalCost) {
         this.totalCost = totalCost;
      }
   }

   public static class Vec3 {
      private final double x;
      private final double y;
      private final double z;

      public Vec3(double x, double y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
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

      public Vec3 addVector(double x, double y, double z) {
         return new Vec3(this.x + x, this.y + y, this.z + z);
      }

      public Vec3 floor() {
         return new Vec3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
      }

      public double squareDistanceTo(Vec3 v) {
         return Math.pow(v.x - this.x, 2.0) + Math.pow(v.y - this.y, 2.0) + Math.pow(v.z - this.z, 2.0);
      }

      public Vec3 add(Vec3 v) {
         return this.addVector(v.getX(), v.getY(), v.getZ());
      }

      public net.minecraft.util.Vec3 mc() {
         return new net.minecraft.util.Vec3(this.x, this.y, this.z);
      }

      public String toString() {
         return "[" + this.x + ";" + this.y + ";" + this.z + "]";
      }
   }
}
