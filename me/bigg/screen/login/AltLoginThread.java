package me.bigg.screen.login;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class AltLoginThread extends Thread {
   private final Minecraft mc = Minecraft.getMinecraft();
   private final String password;
   private String status;
   private final String username;

   public AltLoginThread(String username, String password) {
      super("Alt Login Thread");
      this.username = username;
      this.password = password;
      this.status = "§eWaiting...";
   }

   private final Session createSession(String username, String password) {
      YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
      YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
      auth.setUsername(username);
      auth.setPassword(password);

      try {
         auth.logIn();
         return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
      } catch (AuthenticationException var6) {
         return null;
      }
   }

   public String getStatus() {
      return this.status;
   }

   public void run() {
      if (this.password.equals("")) {
         this.mc.session = new Session(this.username, "", "", "mojang");
         this.status = "§aLogged in. (" + this.username + " - offline name)";
      } else {
         this.status = "§eLogging in...";
         Session auth = this.createSession(this.username, this.password);
         if (auth == null) {
            this.status = "§cLogin failed!";
         } else {
            this.status = "§aLogged in. (" + auth.getUsername() + ")";
            this.mc.session = auth;
         }

      }
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
