package me.bigg.util.sub.particle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;

public class ParticleGenerator {
   private final List particles = new ArrayList();
   private final int amount;
   private int prevWidth;
   private int prevHeight;

   public ParticleGenerator(int amount) {
      this.amount = amount;
   }

   public void draw(int mouseX, int mouseY, int color) {
      if (this.particles.isEmpty() || this.prevWidth != Minecraft.getMinecraft().displayWidth || this.prevHeight != Minecraft.getMinecraft().displayHeight) {
         this.particles.clear();
         this.create();
      }

      this.prevWidth = Minecraft.getMinecraft().displayWidth;
      this.prevHeight = Minecraft.getMinecraft().displayHeight;

      Particle particle;
      for(Iterator var5 = this.particles.iterator(); var5.hasNext(); ParticleRenderUtils.drawCircle(particle.getX(), particle.getY(), particle.size, color)) {
         particle = (Particle)var5.next();
         particle.fall();
         particle.interpolation();
         int range = 50;
         boolean mouseOver = (float)mouseX >= particle.x - (float)range && (float)mouseY >= particle.y - (float)range && (float)mouseX <= particle.x + (float)range && (float)mouseY <= particle.y + (float)range;
         if (mouseOver) {
            this.particles.stream().filter((part) -> {
               return part.getX() > particle.getX() && part.getX() - particle.getX() < (float)range && particle.getX() - part.getX() < (float)range && (part.getY() > particle.getY() && part.getY() - particle.getY() < (float)range || particle.getY() > part.getY() && particle.getY() - part.getY() < (float)range);
            }).forEach((connectable) -> {
               particle.connect(connectable.getX(), connectable.getY(), color);
            });
         }
      }

   }

   private void create() {
      Random random = new Random();

      for(int i = 0; i < this.amount; ++i) {
         this.particles.add(new Particle(random.nextInt(Minecraft.getMinecraft().displayWidth), random.nextInt(Minecraft.getMinecraft().displayHeight)));
      }

   }
}
