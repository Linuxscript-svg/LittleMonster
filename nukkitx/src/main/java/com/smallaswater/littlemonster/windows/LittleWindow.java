package com.smallaswater.littlemonster.windows;

import cn.lanink.gamecore.form.element.ResponseElementButton;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import com.smallaswater.littlemonster.LittleMonsterMainClass;
import com.smallaswater.littlemonster.config.MonsterConfig;
import com.smallaswater.littlemonster.manager.KeyHandleManager;

import java.util.ArrayList;

/**
 * @author SmallasWater
 * Create on 2021/6/30 14:40
 * Package com.smallaswater.littlemonster.windows
 */
public class LittleWindow {

    public static void sendMenu(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("副本主页", "");
        for (String name : LittleMonsterMainClass.getInstance().monsters.keySet()) {
            ResponseElementButton button = new ResponseElementButton(name, new ElementButtonImageData("path", "textures/ui/bad_omen_effect"));
            button.onClicked(cp -> {
                KeyHandleManager.addKey(player, "menu", name);
                LittleWindow.sendSetting(player);
            });
            simple.addButton(button);
        }
        if (simple.getButtons().isEmpty()) {
            simple.setContent("无任何副本信息");
        }

        simple.showToPlayer(player);
    }

    public static void sendSetting(Player player) {
        Object o = KeyHandleManager.getKey(player, "menu");
        if (o != null) {
            MonsterConfig config = LittleMonsterMainClass.getInstance().monsters.get(o.toString());
            if (config != null) {
                AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("副本设置");
                custom.addElement(new ElementInput("请输入血量", "整数血量", config.getHealth() + ""));
                custom.addElement(new ElementInput("请输入攻击力", "整数攻击力", config.getDamage() + ""));
                custom.addElement(new ElementInput("请输入大小", "小数大小", config.getSize() + ""));
                custom.addElement(new ElementInput("请输入移动速度", "小数移动速度", config.getMoveSpeed() + ""));
                custom.addElement(new ElementDropdown("请选择皮肤", new ArrayList<>(LittleMonsterMainClass.loadSkins.keySet())));

                custom.onResponded((formResponseCustom, cp) -> {
                    String h = formResponseCustom.getInputResponse(0);
                    if (h != null && !h.isEmpty()) {
                        config.setHealth(Integer.parseInt(h));
                        config.set("血量", Integer.parseInt(h));
                    }
                    String d = formResponseCustom.getInputResponse(1);
                    if (d != null && !d.isEmpty()) {
                        config.setDamage(Integer.parseInt(d));
                        config.set("攻击力", Integer.parseInt(d));
                    }
                    String s = formResponseCustom.getInputResponse(2);
                    if (s != null && !s.isEmpty()) {
                        config.setSize(Double.parseDouble(s));
                        config.set("大小", Double.parseDouble(s));
                    }
                    String p = formResponseCustom.getInputResponse(3);
                    if (p != null && !p.isEmpty()) {
                        config.setMoveSpeed(Double.parseDouble(p));
                        config.set("移动速度", Double.parseDouble(p));
                    }
                    String k = formResponseCustom.getDropdownResponse(4).getElementContent();
                    if (k != null && !k.isEmpty()) {
                        config.setSkin(k);
                        config.set("皮肤", k);
                    }
                    config.saveAll();
                    config.resetEntity();
                    player.sendMessage("保存完成");
                });

                custom.showToPlayer(player);
            }
        }
    }
}
