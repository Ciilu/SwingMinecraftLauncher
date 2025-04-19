package com.ciluu.smcl.ui;


import com.ciluu.smcl.Main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.Objects;

public class MainFrame extends JFrame {
    public MainFrame() {
        setIconImage(new ImageIcon(Objects.requireNonNull(MainFrame.class.getResource("/icon.png"))).getImage());
        setTitle("Swing Minecraft Launcher " + Main.LAUNCHER_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initMenuBar();

        JPanel panel = new RootPanel(new File("C:\\Users\\admin\\AppData\\Roaming\\.minecraft"));
        setContentPane(panel);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu editMenu = new JMenu("操作");

        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutMenuItem = new JMenuItem("关于");
        JMenuItem githubMenuItem = new JMenuItem("Github");
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "一个基于 HMCLCore 开发的 Minecraft 启动器。\nUI库：Flatlaf",
                    "关于",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        githubMenuItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/Ciilu/SwingMinecraftLauncher"));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        helpMenu.add(githubMenuItem);
        helpMenu.add(aboutMenuItem);

        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
    }
}