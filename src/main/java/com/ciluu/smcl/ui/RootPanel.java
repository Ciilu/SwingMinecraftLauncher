package com.ciluu.smcl.ui;

import com.ciluu.smcl.LaunchHelper;
import com.ciluu.smcl.Settings;
import com.ciluu.smcl.SmclLogger;
import org.jackhuang.hmcl.auth.offline.OfflineAccount;
import org.jackhuang.hmcl.auth.offline.OfflineAccountFactory;
import org.jackhuang.hmcl.game.DefaultGameRepository;
import org.jackhuang.hmcl.game.Version;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;

import static com.ciluu.smcl.LaunchHelper.getAuthlibInjectorArtifactProvider;

public class RootPanel extends JPanel {
    private final DefaultListModel<Version> listModel = new DefaultListModel<>();
    private final JList<Version> listBox = new JList<>(listModel);
    private final GamePane gamePane = new GamePane();
    private DefaultGameRepository repository;

    public RootPanel() {
        setLayout(new BorderLayout());
        loadVersions();

        JScrollPane scrollPane = new JScrollPane(listBox);
        scrollPane.setPreferredSize(new Dimension(200, 0));

        add(scrollPane, BorderLayout.WEST);
        add(gamePane, BorderLayout.CENTER);
    }

    public void refreshVersions() {
        SmclLogger.LOGGER.info("刷新版本列表...");
        listModel.clear();
        loadVersions();
        if (listBox.getSelectedValue() == null) {
            gamePane.showEmptyMessage();
        }
    }

    private void loadVersions() {
        repository = new DefaultGameRepository(new File(Settings.getGameDir()));
        SmclLogger.LOGGER.info("加载版本... 游戏仓库：" + repository.getBaseDirectory());
        repository.refreshVersions();
        Collection<Version> versions = repository.getVersions();

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
                    SmclLogger.LOGGER.info("选中版本：" + selectedVersion.getId());
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
            setLayout(new FlowLayout());
            showEmptyMessage();
        }

        public void update(Version version) {
            removeAll();
            add(new JLabel(version.getId()));
            JButton launchButton = new JButton("开始游戏");
            launchButton.addActionListener((e) -> {
                String userName = Settings.getPlayerName();
                OfflineAccount account = new OfflineAccountFactory(getAuthlibInjectorArtifactProvider())
                        .create(userName, OfflineAccountFactory.getUUIDFromUserName(userName));
                new Thread(() -> {
                    LaunchHelper.launch(repository, version, account, this);
                }).start();
            });
            add(launchButton);
            revalidate();
            repaint();
        }

        public void showEmptyMessage() {
            removeAll();
            add(new JLabel("未选择版本"));
            revalidate();
            repaint();
        }
    }
}