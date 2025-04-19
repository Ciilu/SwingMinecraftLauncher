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
}
