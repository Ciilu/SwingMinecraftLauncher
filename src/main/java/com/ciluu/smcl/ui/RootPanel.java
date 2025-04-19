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
    private final JScrollPane scrollPane = new JScrollPane();

    public RootPanel(File path) {
        super(new FlowLayout(FlowLayout.CENTER, 10, 10));
        this.defaultGameRepository = new DefaultGameRepository(path);
        loadVersions();
    }

    public DefaultGameRepository getDefaultGameRepository() {
        return defaultGameRepository;
    }

    private void loadVersions() {
        SmclLogger.LOGGER.info("加载版本... 游戏仓库：" + defaultGameRepository.getBaseDirectory().toString());
        defaultGameRepository.refreshVersions();
        Collection<Version> versions = defaultGameRepository.getVersions();
        if (versions.isEmpty()) {
            SmclLogger.LOGGER.info("没有找到版本");
            return;
        }
        StringBuilder versionList = new StringBuilder().append("\n");
        versions.forEach(version -> {
            versionList.append("- ").append(version.getId()).append("\n");
        });
        SmclLogger.LOGGER.info(String.format("找到 %d 个版本 列表：%s", versions.size(), versionList));
    }
}
