package com.ciluu.smcl;

import com.ciluu.smcl.ui.MainFrame;
import com.ciluu.smcl.utils.SmclLogger;
import com.formdev.flatlaf.FlatLightLaf;
import org.jackhuang.hmcl.util.SelfDependencyPatcher;

import javax.swing.*;

public class Main {
    public static final String LAUNCHER_VERSION = "0.0.1";

    public static void main(String[] args) throws Exception {
        SelfDependencyPatcher.patch();
        System.gc();

        SmclLogger.LOGGER.info("你好，世界！");
        SmclLogger.LOGGER.info("启动器版本：" + LAUNCHER_VERSION);
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
