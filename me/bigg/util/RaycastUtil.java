package me.bigg.util;

import com.google.common.base.Predicates;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RaycastUtil {
   private static final Minecraft mc = Minecraft.getMinecraft();

   public static Entity raycastEntity(double range, float[] rotations) {
      Entity player = mc.getRenderViewEntity();
      if (player != null && mc.theWorld != null) {
         Vec3 eyeHeight = player.getPositionEyes(mc.timer.renderPartialTicks);
         Vec3 looks = Entity.getVectorForRotation(rotations[0], rotations[1]);
         Vec3 vec = eyeHeight.addVector(looks.xCoord * range, looks.yCoord * range, looks.zCoord * range);
         List list = mc.theWorld.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(looks.xCoord * range, looks.yCoord * range, looks.zCoord * range).expand(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
         Entity raycastedEntity = null;
         Iterator var10 = list.iterator();

         while(true) {
            while(true) {
               Entity entity;
               do {
                  if (!var10.hasNext()) {
                     return raycastedEntity;
                  }

                  entity = (Entity)var10.next();
               } while(!(entity instanceof EntityLivingBase));

               float borderSize = entity.getCollisionBorderSize();
               AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double)borderSize, (double)borderSize, (double)borderSize);
               MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(eyeHeight, vec);
               if (axisalignedbb.isVecInside(eyeHeight)) {
                  if (range >= 0.0) {
                     raycastedEntity = entity;
                     range = 0.0;
                  }
               } else if (movingobjectposition != null) {
                  double distance = eyeHeight.distanceTo(movingobjectposition.hitVec);
                  if (distance < range || range == 0.0) {
                     if (entity == player.ridingEntity) {
                        if (range == 0.0) {
                           raycastedEntity = entity;
                        }
                     } else {
                        raycastedEntity = entity;
                        range = distance;
                     }
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   public static Entity surroundEntity(Entity target) {
      Entity entity = target;
      Iterator var3 = mc.theWorld.loadedEntityList.iterator();

      while(var3.hasNext()) {
         Entity possibleTarget = (Entity)var3.next();
         if (possibleTarget.isInvisible() && !((double)target.getDistanceToEntity(possibleTarget) > 0.5) && mc.thePlayer.getDistanceToEntity(possibleTarget) < mc.thePlayer.getDistanceToEntity(entity)) {
            entity = possibleTarget;
         }
      }

      return target;
   }

   public static BlockPos raycastPosition(double range) {
      Entity renderViewEntity = mc.getRenderViewEntity();
      if (renderViewEntity != null && mc.theWorld != null) {
         MovingObjectPosition movingObjectPosition = renderViewEntity.rayTrace(range, 1.0F);
         return mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock() instanceof BlockAir ? null : movingObjectPosition.getBlockPos();
      } else {
         return null;
      }
   }
}
