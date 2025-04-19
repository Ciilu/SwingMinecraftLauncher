package com.ciluu.smcl.utils;

import org.jackhuang.hmcl.Metadata;
import org.jackhuang.hmcl.auth.authlibinjector.AuthlibInjectorArtifactProvider;
import org.jackhuang.hmcl.auth.offline.OfflineAccount;
import org.jackhuang.hmcl.auth.offline.OfflineAccountFactory;
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
import org.jackhuang.hmcl.util.platform.OperatingSystem;
import org.jackhuang.hmcl.util.versioning.GameVersionNumber;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jackhuang.hmcl.util.Pair.pair;

public class OfflineLaunch {
    private OfflineLaunch() {
    }

    public static void launch(DefaultGameRepository repository, Version version, String userName) {
        OfflineAccount account = new OfflineAccountFactory(getAuthlibInjectorArtifactProvider())
                .create(userName, OfflineAccountFactory.getUUIDFromUserName(userName));
        Profile profile = new Profile(version.getId(), repository.getBaseDirectory());
        try {
            initializeJavaManager();
            LaunchOptions launchOptions = getDefaultLaunchOptions(repository, version, profile);
            HMCLGameLauncher launcher = new HMCLGameLauncher(repository, version, account.playOffline(), launchOptions);
            launcher.launch();
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

    private static AuthlibInjectorArtifactProvider getAuthlibInjectorArtifactProvider() {
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
                .setGameDir(repository.getBaseDirectory())
                .setJava(JavaManager.findSuitableJava(gameVersion, version))
                .setVersionType(Metadata.TITLE)
                .setVersionName(version.getId())
                .setProfileName(Metadata.TITLE)
                .setGameArguments(StringUtils.tokenize(vs.getMinecraftArgs()))
                .setOverrideJavaArguments(StringUtils.tokenize(vs.getJavaArgs()))
                .setMaxMemory(4096)
                .setMinMemory(vs.getMinMemory())
                .setMetaspace(Lang.toIntOrNull(vs.getPermSize()))
                .setEnvironmentVariables(
                        Lang.mapOf(StringUtils.tokenize(vs.getEnvironmentVariables())
                                .stream()
                                .map(it -> {
                                    int idx = it.indexOf('=');
                                    return idx >= 0 ? pair(it.substring(0, idx), it.substring(idx + 1)) : pair(it, "");
                                })
                                .collect(Collectors.toList())
                        )
                )
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
