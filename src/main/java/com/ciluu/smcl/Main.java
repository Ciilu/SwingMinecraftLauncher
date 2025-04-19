package com.ciluu.smcl;

import com.ciluu.smcl.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import org.jackhuang.hmcl.util.SelfDependencyPatcher;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        SelfDependencyPatcher.patch();

        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
