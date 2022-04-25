package me.bigg.screen.shader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public abstract class GLSLShader {
   private int program;
   private Map uniformsMap;
   public static final Minecraft mc = Minecraft.getMinecraft();

   public GLSLShader(String fragmentShader) {
      int vertexShaderID;
      int fragmentShaderID;
      try {
         InputStream vertexStream = this.getClass().getResourceAsStream("/assets/minecraft/annex/globj/vertex.vert");
         vertexShaderID = this.createShader(IOUtils.toString(vertexStream), 35633);
         IOUtils.closeQuietly(vertexStream);
         InputStream fragmentStream = this.getClass().getResourceAsStream("/assets/minecraft/annex/globj/" + fragmentShader);
         fragmentShaderID = this.createShader(IOUtils.toString(fragmentStream), 35632);
         IOUtils.closeQuietly(fragmentStream);
      } catch (Exception var6) {
         var6.printStackTrace();
         return;
      }

      if (vertexShaderID != 0 && fragmentShaderID != 0) {
         this.program = ARBShaderObjects.glCreateProgramObjectARB();
         if (this.program != 0) {
            ARBShaderObjects.glAttachObjectARB(this.program, vertexShaderID);
            ARBShaderObjects.glAttachObjectARB(this.program, fragmentShaderID);
            ARBShaderObjects.glLinkProgramARB(this.program);
            ARBShaderObjects.glValidateProgramARB(this.program);
         }
      }
   }

   public void startShader() {
      GL11.glPushMatrix();
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap();
         this.setupUniforms();
      }

      this.updateUniforms();
   }

   public void renderShader(int width, int height) {
      try {
         this.startShader();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION);
         worldRenderer.pos(0.0, (double)height, 0.0).endVertex();
         worldRenderer.pos((double)width, (double)height, 0.0).endVertex();
         worldRenderer.pos((double)width, 0.0, 0.0).endVertex();
         worldRenderer.pos(0.0, 0.0, 0.0).endVertex();
         instance.draw();
         this.stopShader();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void stopShader() {
      GL20.glUseProgram(0);
      GL11.glPopMatrix();
   }

   public abstract void setupUniforms();

   public abstract void updateUniforms();

   private int createShader(String shaderSource, int shaderType) {
      int shader = 0;

      try {
         shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
         if (shader == 0) {
            return 0;
         } else {
            ARBShaderObjects.glShaderSourceARB(shader, (CharSequence)shaderSource);
            ARBShaderObjects.glCompileShaderARB(shader);
            if (ARBShaderObjects.glGetObjectParameteriARB(shader, 35713) == 0) {
               throw new RuntimeException("Error creating shader: " + this.getLogInfo(shader));
            } else {
               return shader;
            }
         }
      } catch (Exception var5) {
         ARBShaderObjects.glDeleteObjectARB(shader);
         throw var5;
      }
   }

   private String getLogInfo(int i) {
      return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, 35716));
   }

   public void setUniform(String uniformName, int location) {
      this.uniformsMap.put(uniformName, location);
   }

   public void setupUniform(String uniformName) {
      this.setUniform(uniformName, GL20.glGetUniformLocation(this.program, (CharSequence)uniformName));
   }

   public int getUniform(String uniformName) {
      return (Integer)this.uniformsMap.get(uniformName);
   }
}
