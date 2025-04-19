package com.ciluu.smcl.ui;


import com.ciluu.smcl.Main;
import com.ciluu.smcl.Settings;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.Objects;

public class MainFrame extends JFrame {
    private final RootPanel panel;

    public MainFrame() {
        setIconImage(new ImageIcon(Objects.requireNonNull(MainFrame.class.getResource("/icon.png"))).getImage());
        setTitle("Swing Minecraft Launcher " + Main.LAUNCHER_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        panel = new RootPanel();
        initMenuBar();
        setContentPane(panel);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();


        menuBar.add(getEditMenu());
        menuBar.add(getHelpMenu());
        menuBar.add(getSettingMenu());

        this.setJMenuBar(menuBar);
    }

    private JMenu getSettingMenu() {
        JMenu editMenu = new JMenu("设置");
        JMenuItem setGameDirItem = new JMenuItem("修改游戏仓库");
        JMenuItem setPlayerNameItem = new JMenuItem("修改用户名");

        setGameDirItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showOpenDialog(this);
            Settings.setGameDir(fileChooser.getSelectedFile().getAbsolutePath());
            panel.refreshVersions();
        });

        setPlayerNameItem.addActionListener(e -> {
            String userName = JOptionPane.showInputDialog("请输入用户名", Settings.getPlayerName());
            Settings.setPlayerName(userName);
        });
        editMenu.add(setGameDirItem);
        editMenu.add(setPlayerNameItem);

        return editMenu;
    }

    private JMenu getEditMenu() {
        JMenu editMenu = new JMenu("操作");
        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.addActionListener(e -> panel.refreshVersions());
        editMenu.add(refreshItem);
        return editMenu;
    }

    private JMenu getHelpMenu() {
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutMenuItem = new JMenuItem("关于");
        JMenuItem githubMenuItem = new JMenuItem("Github");
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "一个基于 HMCL 底层开发的 Minecraft 启动器。\nUI库：Flatlaf",
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
        return helpMenu;
    }
}