package org.jackhuang.hmcl.setting;

public class ConfigHolder {
    public static GlobalConfig globalConfig() {
        return new GlobalConfig();
    }

    public static Config config() {
        return new Config();
    }
}
