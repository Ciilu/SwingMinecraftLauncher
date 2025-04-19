package com.ciluu.smcl;

import cn.hutool.setting.Setting;

import java.io.File;
import java.io.IOException;

public class Settings {
    private final static Setting SETTING;

    static {
        File f = new File("smcl.setting");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        SETTING = new Setting(f.getAbsolutePath());

        if (SETTING.get("gameDir") == null) {
            SETTING.set("gameDir", new File(".minecraft").getAbsolutePath());
        }

        if (SETTING.get("playerName") == null) {
            SETTING.set("playerName", "Player");
        }

        SETTING.store();
    }

    private Settings() {
    }

    public static String getGameDir() {
        return SETTING.get("gameDir");
    }

    public static void setGameDir(String gameDir) {
        SETTING.set("gameDir", gameDir);
        SETTING.store();
    }

    public static String getPlayerName() {
        return SETTING.get("playerName");
    }

    public static void setPlayerName(String name) {
        SETTING.set("playerName", name);
        SETTING.store();
    }
}
