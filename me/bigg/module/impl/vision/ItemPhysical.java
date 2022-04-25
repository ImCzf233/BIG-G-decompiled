package me.bigg.module.impl.vision;

import java.util.Random;
import me.bigg.module.Category;
import me.bigg.module.Module;
import me.bigg.util.MathUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class ItemPhysical extends Module {
   private static final RenderItem renderItem;
   private static double rotation;
   private static long tick;

   static {
      renderItem = mc.getRenderItem();
   }

   public ItemPhysical() {
      super("Item Physical", "Best mod in the world", Category.Vision);
   }

   public static void doRenderItemPhysic(Entity par1Entity, double x, double y, double z, float par8, float par9) {
      Random random = MathUtil.getRandom();
      rotation = (double)(System.nanoTime() - tick) / 3000000.0;
      if (!mc.inGameHasFocus) {
         rotation = 0.0;
      }

      EntityItem item;
      ItemStack itemstack;
      if ((itemstack = (item = (EntityItem)par1Entity).getEntityItem()).getItem() != null) {
         random.setSeed(187L);
         mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         IBakedModel ibakedmodel = renderItem.getItemModelMesher().getItemModel(itemstack);
         int i = func_177077_a(item, x, y - 0.10000000149011612, z, par9, ibakedmodel);
         BlockPos pos = new BlockPos(item);
         if (item.rotationPitch > 360.0F) {
            item.rotationPitch = 0.0F;
         }

         if (item != null && !Double.isNaN((double)item.getAge()) && !Double.isNaN((double)item.getAir()) && !Double.isNaN((double)item.getEntityId()) && item.getPosition() != null) {
            if (item.onGround) {
               if (item.rotationPitch != 0.0F && item.rotationPitch != 90.0F && item.rotationPitch != 180.0F && item.rotationPitch != 270.0F) {
                  double Abstand0 = formPositive(item.rotationPitch);
                  double Abstand90 = formPositive(item.rotationPitch - 90.0F);
                  double Abstand180 = formPositive(item.rotationPitch - 180.0F);
                  double Abstand270 = formPositive(item.rotationPitch - 270.0F);
                  if (Abstand0 <= Abstand90 && Abstand0 <= Abstand180 && Abstand0 <= Abstand270) {
                     if (item.rotationPitch < 0.0F) {
                        item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                     } else {
                        item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                     }
                  }

                  if (Abstand90 < Abstand0 && Abstand90 <= Abstand180 && Abstand90 <= Abstand270) {
                     if (item.rotationPitch - 90.0F < 0.0F) {
                        item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                     } else {
                        item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                     }
                  }

                  if (Abstand180 < Abstand90 && Abstand180 < Abstand0 && Abstand180 <= Abstand270) {
                     if (item.rotationPitch - 180.0F < 0.0F) {
                        item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                     } else {
                        item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                     }
                  }

                  if (Abstand270 < Abstand90 && Abstand270 < Abstand180 && Abstand270 < Abstand0) {
                     if (item.rotationPitch - 270.0F < 0.0F) {
                        item.rotationPitch = (float)((double)item.rotationPitch + rotation);
                     } else {
                        item.rotationPitch = (float)((double)item.rotationPitch - rotation);
                     }
                  }
               }
            } else {
               BlockPos posUp = new BlockPos(item);
               posUp.add(0.0, 0.20000000298023224, 0.0);
               Material m1 = item.worldObj.getBlockState(posUp).getBlock().getMaterial();
               Material m2 = item.worldObj.getBlockState(pos).getBlock().getMaterial();
               boolean m3 = item.isInsideOfMaterial(Material.water);
               boolean m4 = item.isInWater();
               if (m3 | m1 == Material.water | m2 == Material.water | m4) {
                  item.rotationPitch = (float)((double)item.rotationPitch + rotation / 4.0);
               } else {
                  item.rotationPitch = (float)((double)item.rotationPitch + rotation * 2.0);
               }
            }
         }

         GL11.glRotatef(item.rotationYaw, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(item.rotationPitch + 90.0F, 1.0F, 0.0F, 0.0F);

         for(int j = 0; j < i; ++j) {
            if (ibakedmodel.isAmbientOcclusion()) {
               GlStateManager.pushMatrix();
               GlStateManager.scale(0.7F, 0.7F, 0.7F);
               renderItem.renderItem(itemstack, ibakedmodel);
               GlStateManager.popMatrix();
            } else {
               GlStateManager.pushMatrix();
               if (j > 0 && shouldSpreadItems()) {
                  GlStateManager.translate(0.0F, 0.0F, 0.046875F * (float)j);
               }

               renderItem.renderItem(itemstack, ibakedmodel);
               if (!shouldSpreadItems()) {
                  GlStateManager.translate(0.0F, 0.0F, 0.046875F);
               }

               GlStateManager.popMatrix();
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         mc.getRenderManager().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         mc.getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      }

   }

   public static int func_177077_a(EntityItem item, double x, double y, double z, float p_177077_8_, IBakedModel p_177077_9_) {
      ItemStack itemstack = item.getEntityItem();
      Item item2 = itemstack.getItem();
      if (item2 == null) {
         return 0;
      } else {
         boolean flag = p_177077_9_.isAmbientOcclusion();
         int i = getModelCount(itemstack);
         float f2 = 0.0F;
         GlStateManager.translate((float)x, (float)y + f2 + 0.25F, (float)z);
         float f3 = 0.0F;
         if (flag || mc.getRenderManager().renderEngine != null && mc.gameSettings.fancyGraphics) {
            GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
         }

         if (!flag) {
            f3 = -0.0F * (float)(i - 1) * 0.5F;
            float f4 = -0.0F * (float)(i - 1) * 0.5F;
            float f5 = -0.046875F * (float)(i - 1) * 0.5F;
            GlStateManager.translate(f3, f4, f5);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         return i;
      }
   }

   public static int getModelCount(ItemStack stack) {
      byte b0 = 1;
      if (stack.animationsToGo > 48) {
         b0 = 5;
      } else if (stack.animationsToGo > 32) {
         b0 = 4;
      } else if (stack.animationsToGo > 16) {
         b0 = 3;
      } else if (stack.animationsToGo > 1) {
         b0 = 2;
      }

      return b0;
   }

   public static boolean shouldSpreadItems() {
      return true;
   }

   public static double formPositive(float rotationPitch) {
      return rotationPitch > 0.0F ? (double)rotationPitch : (double)(-rotationPitch);
   }
}
