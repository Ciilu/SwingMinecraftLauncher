package com.ciluu.smcl.ui;

import com.ciluu.smcl.utils.SmclLogger;
import org.jackhuang.hmcl.game.DefaultGameRepository;
import org.jackhuang.hmcl.game.Version;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;

public class RootPanel extends JPanel {
    private final DefaultGameRepository defaultGameRepository;
    private final DefaultListModel<Version> listModel = new DefaultListModel<>();
    private final JList<Version> listBox = new JList<>(listModel);
    private final GamePane gamePane = new GamePane();

    public RootPanel(File path) {
        setLayout(new BorderLayout());
        defaultGameRepository = new DefaultGameRepository(path);
        loadVersions();

        JScrollPane scrollPane = new JScrollPane(listBox);
        scrollPane.setPreferredSize(new Dimension(200, 0));

        add(scrollPane, BorderLayout.WEST);
        add(gamePane, BorderLayout.CENTER);
    }

    private void loadVersions() {
        SmclLogger.LOGGER.info("加载版本... 游戏仓库：" + defaultGameRepository.getBaseDirectory());
        defaultGameRepository.refreshVersions();
        Collection<Version> versions = defaultGameRepository.getVersions();

        if (versions.isEmpty()) {
            SmclLogger.LOGGER.info("没有找到版本");
            return;
        }

        StringBuilder versionList = new StringBuilder().append("\n");
        versions.forEach(version -> versionList.append("- ").append(version.getId()).append("\n"));
        SmclLogger.LOGGER.info(String.format("找到 %d 个版本 列表：%s", versions.size(), versionList));
        versions.forEach(listModel::addElement);

        listBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listBox.setCellRenderer(new VersionListCellRenderer());

        listBox.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Version selectedVersion = listBox.getSelectedValue();
                if (selectedVersion != null) {
                    gamePane.update(selectedVersion);
                } else {
                    gamePane.showEmptyMessage();
                }
            }
        });
    }

    private static class VersionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Version version = (Version) value;
            label.setText(version.getId());
            return label;
        }
    }

    private class GamePane extends JPanel {
        public GamePane() {
            setLayout(new BorderLayout());
            showEmptyMessage();
        }

        public void update(Version version) {
            removeAll();
            add(new JLabel("已选择版本：" + version.getId()), BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        public void showEmptyMessage() {
            removeAll();
            add(new JLabel("未选择版本"), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }
}