package com.ciluu.smcl;

import org.jackhuang.hmcl.auth.Account;
import org.jackhuang.hmcl.auth.authlibinjector.AuthlibInjectorArtifactProvider;
import org.jackhuang.hmcl.download.LibraryAnalyzer;
import org.jackhuang.hmcl.game.*;
import org.jackhuang.hmcl.java.JavaManager;
import org.jackhuang.hmcl.java.JavaRuntime;
import org.jackhuang.hmcl.setting.Accounts;
import org.jackhuang.hmcl.setting.Profile;
import org.jackhuang.hmcl.setting.VersionSetting;
import org.jackhuang.hmcl.util.CacheRepository;
import org.jackhuang.hmcl.util.Lang;
import org.jackhuang.hmcl.util.StringUtils;
import org.jackhuang.hmcl.util.versioning.GameVersionNumber;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class LaunchHelper {
    private LaunchHelper() {
    }

    public static void launch(DefaultGameRepository repository, Version version, Account account, JPanel panel) {
        Profile profile = new Profile(version.getId(), repository.getBaseDirectory());
        try {
            initializeJavaManager();
            LaunchOptions launchOptions = getDefaultLaunchOptions(repository, version, profile);
            HMCLGameLauncher launcher = new HMCLGameLauncher(repository, version, account.logIn(), launchOptions);
            launcher.launch();
            JOptionPane.showMessageDialog(panel, "游戏已启动");
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void initializeJavaManager() throws Exception {
        CacheRepository.setInstance(HMCLCacheRepository.REPOSITORY);
        Class<JavaManager> javaManagerClass = JavaManager.class;
        Method method = javaManagerClass.getDeclaredMethod("searchPotentialJavaExecutables");
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Path, JavaRuntime> allJava = (Map<Path, JavaRuntime>) method.invoke(null);
        Field f = javaManagerClass.getDeclaredField("allJava");
        f.setAccessible(true);
        f.set(allJava, allJava);
    }

    public static AuthlibInjectorArtifactProvider getAuthlibInjectorArtifactProvider() {
        Class<Accounts> accountsClass = Accounts.class;
        try {
            Method method = accountsClass.getDeclaredMethod("createAuthlibInjectorArtifactProvider");
            method.setAccessible(true);
            return (AuthlibInjectorArtifactProvider) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static LaunchOptions getDefaultLaunchOptions(DefaultGameRepository repository, Version version, Profile profile) throws Exception {
        VersionSetting vs = new VersionSetting();
        GameVersionNumber gameVersion = GameVersionNumber.asGameVersion(LibraryAnalyzer.analyze(version, version.getVersion()).getVersion(LibraryAnalyzer.LibraryType.MINECRAFT));

        LaunchOptions.Builder builder = new LaunchOptions.Builder()
                .setGameDir(repository.getRunDirectory(version.getId()))
                .setJava(JavaManager.findSuitableJava(gameVersion, version))
                .setVersionType("Swing Minecraft Launcher")
                .setVersionName(version.getId())
                .setProfileName("Swing Minecraft Launcher")
                .setGameArguments(StringUtils.tokenize(vs.getMinecraftArgs()))
                .setOverrideJavaArguments(StringUtils.tokenize(vs.getJavaArgs()))
                .setMaxMemory(4096)
                .setMinMemory(vs.getMinMemory())
                .setMetaspace(Lang.toIntOrNull(vs.getPermSize()))
                .setWidth(vs.getWidth())
                .setHeight(vs.getHeight())
                .setFullscreen(vs.isFullscreen())
                .setServerIp(vs.getServerIp())
                .setWrapper(vs.getWrapper())
                .setPreLaunchCommand(vs.getPreLaunchCommand())
                .setPostExitCommand(vs.getPostExitCommand())
                .setNoGeneratedJVMArgs(vs.isNoJVMArgs())
                .setNativesDirType(vs.getNativesDirType())
                .setNativesDir(vs.getNativesDir())
                .setProcessPriority(vs.getProcessPriority())
                .setRenderer(vs.getRenderer())
                .setUseNativeGLFW(vs.isUseNativeGLFW())
                .setUseNativeOpenAL(vs.isUseNativeOpenAL())
                .setDaemon(vs.getLauncherVisibility().isDaemon())
                .setJavaAgents(new ArrayList<>())
                .setJavaArguments(new ArrayList<>());
        return builder.create();
    }
}
