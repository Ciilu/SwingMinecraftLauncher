package com.ciluu.smcl;

import com.ciluu.smcl.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import org.jackhuang.hmcl.game.HMCLCacheRepository;
import org.jackhuang.hmcl.util.CacheRepository;
import org.jackhuang.hmcl.util.SelfDependencyPatcher;

import javax.swing.*;
import java.lang.reflect.Method;

public class Main {
    public static final String LAUNCHER_VERSION = "0.0.1";

    public static void main(String[] args) throws Exception {
        SelfDependencyPatcher.patch();
        System.gc();

        Class<?> platformClass = Class.forName("javafx.application.Platform");
        Method startupMethod = platformClass.getMethod("startup", Runnable.class);
        startupMethod.invoke(null, (Runnable) () -> {
        });
        CacheRepository.setInstance(HMCLCacheRepository.REPOSITORY);
        SmclLogger.LOGGER.info("你好，世界！");
        SmclLogger.LOGGER.info("启动器版本：" + LAUNCHER_VERSION);
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
