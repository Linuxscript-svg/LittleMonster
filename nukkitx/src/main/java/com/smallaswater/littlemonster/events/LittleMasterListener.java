package com.smallaswater.littlemonster.events;


import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.server.ServerStopEvent;
import com.smallaswater.littlemonster.LittleMonsterMainClass;
import com.smallaswater.littlemonster.entity.IEntity;
import com.smallaswater.littlemonster.entity.LittleNpc;
import com.smallaswater.littlemonster.entity.vanilla.VanillaNPC;
import com.smallaswater.littlemonster.manager.KeyHandleManager;
import com.smallaswater.littlemonster.manager.TimerHandleManager;
import com.smallaswater.littlemonster.threads.PluginMasterThreadPool;


/**
 * @author SmallasWater
 * Create on 2021/6/28 8:58
 * Package com.smallaswater.littlemonster.events
 */
public class LittleMasterListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e instanceof EntityDamageByEntityEvent){
            Entity entity = e.getEntity();
            //Entity d = ((EntityDamageByEntityEvent) e).getDamager();
            if(entity instanceof LittleNpc || entity instanceof VanillaNPC){
                //卡墙修复
                if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
                    entity.teleport(entity.add(0,2));
                }
                //迁移到 LittleNpc#onAttack() 方法
                /*if(d instanceof Player){
                    ((LittleNpc) entity).handle.add(d.getName(),e.getDamage());
                }*/
                if (LittleMonsterMainClass.hasRcRPG) return;// 有 RcRPG 时无需处理伤害事件
                if(entity instanceof LittleNpc){
                    float damage = e.getDamage() - ((LittleNpc) entity).getConfig().getDelDamage();
                    if(damage < 0){
                        damage = 0;
                    }
                    e.setDamage(damage);
                }else{
                    float damage = e.getDamage() - ((VanillaNPC) entity).getConfig().getDelDamage();
                    if(damage < 0){
                        damage = 0;
                    }
                    e.setDamage(damage);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(KeyHandleManager.isKey(player,"Ice") && TimerHandleManager.getTimeHandle(player).hasCold("Ice")){
            player.sendPopup("你被冰冻啦");
            event.setCancelled();
        }else{
            KeyHandleManager.removeKey(player,"Ice");
        }
    }

    @EventHandler
    public void onDie(EntityDeathEvent e){
        Entity entity = e.getEntity();
        if (entity instanceof IEntity) {
            ((IEntity) entity).onDeath(e);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities().values()) {
            if (entity instanceof LittleNpc) {
                entity.close();
            }
        }
    }

    @EventHandler
    public void onServerStop(ServerStopEvent event) {
//        RouteFinderThreadPool.shutDownNow();
        PluginMasterThreadPool.shutDownNow();
    }
}
