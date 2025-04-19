package com.ciluu.smcl.ui;

import com.ciluu.smcl.Settings;
import com.ciluu.smcl.SmclLogger;
import org.jackhuang.hmcl.download.BMCLAPIDownloadProvider;
import org.jackhuang.hmcl.download.DefaultDependencyManager;
import org.jackhuang.hmcl.download.DownloadProvider;
import org.jackhuang.hmcl.download.game.GameRemoteVersion;
import org.jackhuang.hmcl.download.game.GameVersionList;
import org.jackhuang.hmcl.game.DefaultGameRepository;
import org.jackhuang.hmcl.game.ReleaseType;
import org.jackhuang.hmcl.setting.Profile;
import org.jackhuang.hmcl.task.Task;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.stream.Collectors;

public class InstallDialog extends JDialog {
    private final DefaultGameRepository repository = new DefaultGameRepository(new File(Settings.getGameDir()));
    private final DownloadProvider downloadProvider = new BMCLAPIDownloadProvider("https://bmclapi2.bangbang93.com");
    private final GameVersionList gameVersionList = new GameVersionList(downloadProvider);
    private final JLabel loadingLabel = new JLabel("加载中...");
    private final MainFrame parent;
    private final JTextField name = new JTextField();

    public InstallDialog(MainFrame parent) {
        super(parent, "安装新版本", true);
        this.parent = parent;
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        add(loadingLabel, BorderLayout.CENTER);
        loadingLabel.setVisible(true);
        gameVersionList.refreshAsync().thenRun(this::init);
    }

    private void init() {
        SwingUtilities.invokeLater(() -> {
            loadingLabel.setVisible(false);
            setLayout(new FlowLayout());
            add(new JLabel("版本名称"));
            add(name);
            add(new JPanel());
            add(new JLabel("Minecraft:"));
            DefaultComboBoxModel<GameRemoteVersion> model = new DefaultComboBoxModel<>();
            model.addAll(gameVersionList.getVersions("").stream().filter((e) -> e.getType() == ReleaseType.RELEASE).collect(Collectors.toList()));
            JComboBox<GameRemoteVersion> gameVersionComboBox = new JComboBox<>(model);
            gameVersionComboBox.setRenderer(new VersionListCellRenderer());
            gameVersionComboBox.setMaximumSize(new Dimension(30, gameVersionComboBox.getPreferredSize().height));
            add(gameVersionComboBox);
            add(new JPanel());
            JButton installButton = new JButton("安装");
            installButton.addActionListener(e -> {
                onInstall((GameRemoteVersion) gameVersionComboBox.getSelectedItem());
            });
            add(installButton);
            revalidate();
        });
    }

    private void onInstall(GameRemoteVersion gameVersion) {
        if (gameVersion != null) {
            Profile profile = new Profile(gameVersion.getGameVersion(), repository.getBaseDirectory());
            DefaultDependencyManager dependencyManager = profile.getDependency(downloadProvider);
            try {
                dependencyManager.getDownloadProvider().getVersionListById("game").refreshAsync().get();
            } catch (Exception e) {
                SmclLogger.LOGGER.severe(e.getMessage());
            }
            try {
                Task<?> task = dependencyManager.gameBuilder()
                        .name(this.name.getText())
                        .version(gameVersion)
                        .gameVersion(gameVersion.getGameVersion())
                        .buildAsync()
                        .whenComplete(Throwable::printStackTrace)
                        .thenRunAsync(parent.panel::refreshVersions);
                task.run();
            } catch (Throwable e) {
                this.setVisible(false);
                parent.panel.refreshVersions();
                JOptionPane.showMessageDialog(parent, "安装完成");
            }
        }
    }

    private static class VersionListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                GameRemoteVersion version = (GameRemoteVersion) value;
                label.setText(version.getGameVersion());
            }
            return label;
        }
    }
}
