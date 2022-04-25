package me.bigg.module;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import me.bigg.event.KeyTypedEvent;
import me.bigg.module.assist.NetWorkAssist;
import me.bigg.module.assist.ScaffoldAssist;
import me.bigg.module.impl.combat.AntiBot;
import me.bigg.module.impl.combat.AutoPot;
import me.bigg.module.impl.combat.Criticals;
import me.bigg.module.impl.combat.KillAura;
import me.bigg.module.impl.combat.Velocity;
import me.bigg.module.impl.ghost.Eagle;
import me.bigg.module.impl.ghost.FastPlace;
import me.bigg.module.impl.ghost.Reach;
import me.bigg.module.impl.misc.BedBreaker;
import me.bigg.module.impl.misc.Clown;
import me.bigg.module.impl.misc.Disabler;
import me.bigg.module.impl.misc.GamePlay;
import me.bigg.module.impl.misc.HackerDetect;
import me.bigg.module.impl.misc.Scaffold;
import me.bigg.module.impl.misc.Teams;
import me.bigg.module.impl.movement.GuiMove;
import me.bigg.module.impl.movement.Lagback;
import me.bigg.module.impl.movement.NoSlow;
import me.bigg.module.impl.movement.Sprint;
import me.bigg.module.impl.movement.Step;
import me.bigg.module.impl.movement.Strafe;
import me.bigg.module.impl.movement.TargetStrafe;
import me.bigg.module.impl.movement.fly.Fly;
import me.bigg.module.impl.movement.longjump.LongJump;
import me.bigg.module.impl.movement.phase.Phase;
import me.bigg.module.impl.movement.speed.Speed;
import me.bigg.module.impl.player.AutoArmor;
import me.bigg.module.impl.player.AutoTool;
import me.bigg.module.impl.player.ChestStealer;
import me.bigg.module.impl.player.FastMine;
import me.bigg.module.impl.player.FastUse;
import me.bigg.module.impl.player.InvManager;
import me.bigg.module.impl.player.NoFall;
import me.bigg.module.impl.vision.Arrow;
import me.bigg.module.impl.vision.CGui;
import me.bigg.module.impl.vision.CamNoClip;
import me.bigg.module.impl.vision.Cape;
import me.bigg.module.impl.vision.Chams;
import me.bigg.module.impl.vision.Crosshair;
import me.bigg.module.impl.vision.DMGParticle;
import me.bigg.module.impl.vision.FPSHurtCam;
import me.bigg.module.impl.vision.FullBright;
import me.bigg.module.impl.vision.HUD;
import me.bigg.module.impl.vision.ItemPhysical;
import me.bigg.module.impl.vision.NameTag;
import me.bigg.module.impl.vision.NoHurtCam;
import me.bigg.module.impl.vision.NoPumpkin;
import me.bigg.module.impl.vision.OldHitting;
import me.bigg.module.impl.vision.TargetHud;
import me.bigg.module.impl.vision.TimeChanger;
import me.bigg.module.impl.vision.Xray;

public class ModuleManager {
   private final ArrayList moduleList = new ArrayList();
   private final ScaffoldAssist scaffoldAssist;
   private final NetWorkAssist netWorkAssist;

   public ModuleManager() {
      EventManager.register(this);
      this.moduleList.add(new Criticals());
      this.moduleList.add(new KillAura());
      this.moduleList.add(new Velocity());
      this.moduleList.add(new AntiBot());
      this.moduleList.add(new AutoPot());
      this.moduleList.add(new TargetStrafe());
      this.moduleList.add(new LongJump());
      this.moduleList.add(new Scaffold());
      this.moduleList.add(new Lagback());
      this.moduleList.add(new GuiMove());
      this.moduleList.add(new NoSlow());
      this.moduleList.add(new Sprint());
      this.moduleList.add(new Strafe());
      this.moduleList.add(new Phase());
      this.moduleList.add(new Speed());
      this.moduleList.add(new Step());
      this.moduleList.add(new Fly());
      this.moduleList.add(new ItemPhysical());
      this.moduleList.add(new TimeChanger());
      this.moduleList.add(new DMGParticle());
      this.moduleList.add(new FPSHurtCam());
      this.moduleList.add(new OldHitting());
      this.moduleList.add(new FullBright());
      this.moduleList.add(new TargetHud());
      this.moduleList.add(new NoPumpkin());
      this.moduleList.add(new Crosshair());
      this.moduleList.add(new NoHurtCam());
      this.moduleList.add(new CamNoClip());
      this.moduleList.add(new NameTag());
      this.moduleList.add(new Arrow());
      this.moduleList.add(new Chams());
      this.moduleList.add(new Cape());
      this.moduleList.add(new CGui());
      this.moduleList.add(new HUD());
      this.moduleList.add(new Xray());
      this.moduleList.add(new ChestStealer());
      this.moduleList.add(new InvManager());
      this.moduleList.add(new AutoArmor());
      this.moduleList.add(new AutoTool());
      this.moduleList.add(new FastMine());
      this.moduleList.add(new FastUse());
      this.moduleList.add(new NoFall());
      this.moduleList.add(new FastPlace());
      this.moduleList.add(new Eagle());
      this.moduleList.add(new Reach());
      this.moduleList.add(new HackerDetect());
      this.moduleList.add(new BedBreaker());
      this.moduleList.add(new GamePlay());
      this.moduleList.add(new Disabler());
      this.moduleList.add(new Clown());
      this.moduleList.add(new Teams());
      this.scaffoldAssist = new ScaffoldAssist();
      this.scaffoldAssist.init();
      this.netWorkAssist = new NetWorkAssist();
      this.netWorkAssist.init();
   }

   public ArrayList getModuleList() {
      return this.moduleList;
   }

   public Module getModule(String name) {
      Iterator var3 = this.moduleList.iterator();

      while(var3.hasNext()) {
         Module m = (Module)var3.next();
         if (m.getSimpleName().equalsIgnoreCase(name)) {
            return m;
         }
      }

      return null;
   }

   public NetWorkAssist getNetWorkAssist() {
      return this.netWorkAssist;
   }

   @EventTarget
   void onKeyTyped(KeyTypedEvent event) {
      Iterator var3 = this.moduleList.iterator();

      while(var3.hasNext()) {
         Module m = (Module)var3.next();
         if (m.getKeybinding() == event.getKey()) {
            m.toggle();
         }
      }

   }
}
